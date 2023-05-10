package io.junnyland.realworld.global.security

import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono


class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider,
    private val jwtauthenticationManager: ReactiveAuthenticationManager,
) : AuthenticationWebFilter(JwtAuthenticationManager(jwtTokenProvider)) {

    override fun onAuthenticationSuccess(
        authentication: Authentication,
        webFilterExchange: WebFilterExchange,
    ): Mono<Void> = webFilterExchange.chain
        .filter(
            createHeader(webFilterExchange, authentication)
                .build()
        )

    private fun createHeader(
        webFilterExchange: WebFilterExchange,
        authentication: Authentication,
    ) = webFilterExchange.exchange
        .mutate()
        .request(
            webFilterExchange.exchange.request.mutate()
                .header(AUTHORIZATION, "Bearer ${authentication.token}")
                .build()
        )

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> =
        jwtauthenticationManager
            .authenticate(jwtTokenProvider.getAuthentication(exchange.token()))
            .flatMap { authentication -> super.filter(exchange, chain)
                    .then(
                        onAuthenticationSuccess(
                            authentication,
                            WebFilterExchange(exchange, chain)
                        )
                    )
            }
            .onErrorResume(AuthenticationException::class.java) {
                chain.filter(exchange)

            }
}

private val Authentication.token: String get() = this.credentials.toString()