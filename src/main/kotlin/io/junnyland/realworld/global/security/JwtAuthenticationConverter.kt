package io.junnyland.realworld.global.security

import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono


class AuthenticationWebFilter(
    private val jwtTokenProvider: JwtTokenProvider,
) : ServerAuthenticationConverter {
    override fun convert(exchange: ServerWebExchange): Mono<Authentication> = takeIf { jwtTokenProvider.validateToken(exchange.token()) }
            ?.let { Mono.just(jwtTokenProvider.getAuthentication(exchange.token())) }
            ?: Mono.empty()
}

fun ServerWebExchange.token(): String = this.request.headers.getFirst(HttpHeaders.AUTHORIZATION)
        ?.withOutBearer
        ?: throw AuthenticationCredentialsNotFoundException("Token not found")


private val String.withOutBearer get() = this.substring(7)