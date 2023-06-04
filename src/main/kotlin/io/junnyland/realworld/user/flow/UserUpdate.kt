package io.junnyland.realworld.user.flow

import io.junnyland.realworld.global.security.tokenPrefix
import io.junnyland.realworld.user.action.out.repository.UserRepository
import io.junnyland.realworld.user.action.out.security.TokenParser
import io.junnyland.realworld.user.action.out.security.TokenProvider
import io.junnyland.realworld.user.domain.User
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

interface UserUpdate {
    fun fetch(target:Mono<User>): Mono<User>

    @Service
    class UserUpdateUsecase(
        private val userRepository: UserRepository,
        private val tokenParser: TokenParser,
        private val tokenProvider: TokenProvider,
        private val authenticationManager: ReactiveAuthenticationManager,
    ) : UserUpdate {
        override fun fetch(target: Mono<User>) = target
            .delayUntil {userRepository.fetch(tokenParser.extract(it.token.tokenPrefix()).name, it) }
            .map { UsernamePasswordAuthenticationToken(it.email, it.password) }
            .flatMap { token -> authenticationManager.authenticate(token) }
            .map { authenticate -> authenticate.name to tokenProvider.generate(authenticate) }
            .flatMap { userInfo:UserAndToken -> userRepository.renewToken(userInfo.name, userInfo.token) }
    }
}

