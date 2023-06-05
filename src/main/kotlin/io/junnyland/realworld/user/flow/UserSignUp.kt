package io.junnyland.realworld.user.flow

import io.junnyland.realworld.user.action.out.repository.UserRepository
import io.junnyland.realworld.user.action.out.security.TokenProvider
import io.junnyland.realworld.user.domain.User
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono


fun interface UserSignUp {
    fun signUp(request: Mono<SignUpRequest>): Mono<User>

    @Service
    class UserSignUpUsecase(
        private val userRepository: UserRepository,
        private val tokenProvider: TokenProvider,
        private val authenticationManager: ReactiveAuthenticationManager,
    ) : UserSignUp {
        override fun signUp(request: Mono<SignUpRequest>): Mono<User> = request
            .map(SignUpRequest::toUser)
            .delayUntil { userRepository.save(it)  }
            .map { UsernamePasswordAuthenticationToken(it.email, it.password) }
            .flatMap { token -> authenticationManager.authenticate(token) }
            .map { authenticate -> authenticate.name to tokenProvider.generate(authenticate) }
            .flatMap { userInfo: UserAndToken -> userRepository.renewToken(userInfo.name, userInfo.token) }
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