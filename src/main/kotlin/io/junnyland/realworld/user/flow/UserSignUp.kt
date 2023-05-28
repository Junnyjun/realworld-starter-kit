package io.junnyland.realworld.user.flow

import io.junnyland.realworld.user.action.out.repository.UserRepository
import io.junnyland.realworld.user.action.out.security.TokenProvider
import io.junnyland.realworld.user.domain.User
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

private typealias EmailAndToken = Pair<String, String>

private fun EmailAndToken.name() = this.first
private fun EmailAndToken.token() = this.second

interface UserSignUp {
    fun signUp(request: Mono<SignUpRequest>): Mono<User>

    @Service
    class UserSignUpUsecase(
        private val userRepository: UserRepository,
        private val tokenProvider: TokenProvider,
        private val authenticationManager: ReactiveAuthenticationManager,
    ) : UserSignUp {
        override fun signUp(request: Mono<SignUpRequest>): Mono<User> = request
            .map { it.toUser() }
            .flatMap { userRepository.save(it)
                .flatMap { savedUser ->
                    Mono.just(it) // request를 Mono로 감싸서 반환
                        .map { request -> UsernamePasswordAuthenticationToken(request.email, request.password) }
                        .flatMap { authenticationManager.authenticate(it) }
                        .map { authenticate -> authenticate.name to tokenProvider.generate(authenticate) }
                        .flatMap { userInfo -> userRepository.renewToken(userInfo.first, userInfo.second) }
                }
            }
    }

    data class SignUpRequest(
        val email: String,
        val username: String,
        val password: String,
    ) {
        fun toUser(): User = User(
            email = email,
            username = username,
            password = password
        )
    }
}


