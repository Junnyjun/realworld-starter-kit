package io.junnyland.realworld.user.action.`in`.http.security

import io.junnyland.realworld.user.action.out.security.TokenParser
import io.junnyland.realworld.user.action.out.security.TokenValidator
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono


class JwtAuthenticationFilter(
    private val parser: TokenParser,
    private val validate: TokenValidator,
    private val jwtauthenticationManager: ReactiveAuthenticationManager,
    private val converter: AuthenticationConverter,
) : AuthenticationWebFilter(JwtAuthenticationManager(parser, validate)) {
    private val Authentication.token: String get() = this.credentials.toString()
    private val ServerWebExchange.token: String get() = this.request.headers.getFirst(AUTHORIZATION) ?: ""

    override fun onAuthenticationSuccess(
        authentication: Authentication, webFilterExchange: WebFilterExchange): Mono<Void> = webFilterExchange.chain
        .filter(createHeader(webFilterExchange, authentication).build())

    override fun setServerAuthenticationConverter(authenticationConverter: ServerAuthenticationConverter) {
        super.setServerAuthenticationConverter(converter)
    }

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
            .authenticate(parser.extract(exchange.token))
            .flatMap { authentication ->
                super
                    .filter(exchange, chain)
                    .then(
                        onAuthenticationSuccess(
                            authentication,
                            WebFilterExchange(exchange, chain)
                        )
                    )
            }
            .onErrorResume(AuthenticationException::class.java) { chain.filter(exchange) }
}

