package io.junnyland.realworld.user.action.`in`.http.security

import io.junnyland.realworld.user.action.out.security.TokenParser
import io.junnyland.realworld.user.action.out.security.TokenValidator
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.Authentication
import reactor.core.publisher.Mono
import reactor.core.publisher.Mono.empty
import reactor.core.publisher.Mono.just

class JwtAuthenticationManager(
    private val parser: TokenParser,
    private val validate: TokenValidator,
) : ReactiveAuthenticationManager {

    override fun authenticate(authentication: Authentication): Mono<Authentication> =
        authentication.takeIf { validate.by(it.credential) }
            ?.let { just(parser.extract(authentication.credential)) }
            ?: empty()
}

private val Authentication.credential: String get() = this.credentials.toString()