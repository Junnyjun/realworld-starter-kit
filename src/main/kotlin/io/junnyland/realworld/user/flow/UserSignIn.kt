package io.junnyland.realworld.user.flow

import io.junnyland.realworld.user.action.out.repository.UserRepository
import io.junnyland.realworld.user.action.out.security.TokenProvider
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

interface UserSignIn {
    fun login(request: Mono<SignInRequest>): Mono<Token>

    @Service
    class UserSignInUsecase(
        private val authenticationManager: ReactiveAuthenticationManager,
        private val tokenProvider: TokenProvider,
        private val userRepository: UserRepository,
    ) : UserSignIn {
        override fun login(request: Mono<SignInRequest>): Mono<Token> = request
            .map { UsernamePasswordAuthenticationToken(it.email, it.password) }
            .flatMap { authenticationManager.authenticate(it) }
            .map {
                authentication ->
                val token = tokenProvider.generate(authentication)
                userRepository.renewToken(authentication.name,token)
            }
            .map { Token(it) }
    }

    data class SignInRequest(
        val email: String,
        val password: String,
    )

    data class Token(
        val token: String,
    )
}

