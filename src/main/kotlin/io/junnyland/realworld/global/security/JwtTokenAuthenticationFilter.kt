package io.junnyland.realworld.global.security

import io.junnyland.realworld.user.action.out.security.TokenParser
import io.junnyland.realworld.user.action.out.security.TokenValidator
import org.springframework.http.HttpHeaders
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.security.core.context.ReactiveSecurityContextHolder.withAuthentication
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

const val HEADER_PREFIX = "Token "
fun String.tokenPrefix(): String = this.removePrefix(HEADER_PREFIX)

class JwtTokenAuthenticationFilter(
    private val validator: TokenValidator,
    private val parser: TokenParser,
) : WebFilter {


    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> =
        resolveToken(exchange.request)
            ?.takeIf { validator.by(it) }
            ?.let { parser.extract(it) }
            ?.let { chain.filter(exchange).contextWrite(withAuthentication(it)) }
            ?: chain.filter(exchange)

    private fun resolveToken(request: ServerHttpRequest): String? =
        request.headers.getFirst(HttpHeaders.AUTHORIZATION)
            ?.takeIf { it.startsWith(HEADER_PREFIX) }
            ?.removePrefix(HEADER_PREFIX)
}