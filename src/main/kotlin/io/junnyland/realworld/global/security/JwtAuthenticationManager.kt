package io.junnyland.realworld.global.security

import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.Authentication
import reactor.core.publisher.Mono
import reactor.core.publisher.Mono.empty
import reactor.core.publisher.Mono.just

class JwtAuthenticationManager(
    private val jwtTokenProvider: JwtTokenProvider,
) : ReactiveAuthenticationManager {

    override fun authenticate(authentication: Authentication): Mono<Authentication> =
        authentication.takeIf { jwtTokenProvider.validateToken(it.credential) }
            ?.let { just(jwtTokenProvider.getAuthentication(authentication.credential)) }
            ?: empty()
}

private val Authentication.credential: String get() = this.credentials.toString()