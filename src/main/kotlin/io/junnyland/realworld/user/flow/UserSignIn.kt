package io.junnyland.realworld.user.flow

import io.junnyland.realworld.user.action.out.repository.UserRepository
import io.junnyland.realworld.user.action.out.security.TokenProvider
import io.junnyland.realworld.user.domain.User
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

private typealias UserAndToken = Pair<String, String>

private fun UserAndToken.name() = this.first
private fun UserAndToken.token() = this.second

interface UserSignIn {
    fun login(request: Mono<SignInRequest>): Mono<User>

    @Service
    class UserSignInUsecase(
        private val authenticationManager: ReactiveAuthenticationManager,
        private val passwordEncoder: PasswordEncoder,
        private val tokenProvider: TokenProvider,
        private val userRepository: UserRepository,
    ) : UserSignIn {
        override fun login(request: Mono<SignInRequest>): Mono<User> = request
            .map { UsernamePasswordAuthenticationToken(it.email, it.password) }
            .flatMap { authenticationManager.authenticate(it) }
            .map { authenticate -> authenticate.name to tokenProvider.generate(authenticate) }
            .flatMap { userInfo: UserAndToken -> userRepository.renewToken(userInfo.name(), userInfo.token()) }
    }

    data class SignInRequest(
        val email: String,
        val password: String,
    )
}


