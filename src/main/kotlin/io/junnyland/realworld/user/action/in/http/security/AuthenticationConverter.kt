package io.junnyland.realworld.user.action.`in`.http.security

import io.junnyland.realworld.user.action.out.security.TokenParser
import io.junnyland.realworld.user.action.out.security.TokenValidator
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono


class AuthenticationConverter(
    private val parser: TokenParser,
    private val validate: TokenValidator,
) : ServerAuthenticationConverter {
    private val String.withOutBearer get() = this.substring(7)

    override fun convert(exchange: ServerWebExchange): Mono<Authentication> =
        takeIf { validate.by(exchange.token()) }
            ?.let { Mono.just(parser.extract(exchange.token())) }
            ?: Mono.empty()

    fun ServerWebExchange.token(): String = this.request.headers.getFirst(HttpHeaders.AUTHORIZATION)
        ?.withOutBearer
        ?: throw AuthenticationCredentialsNotFoundException("Token not found")
}
