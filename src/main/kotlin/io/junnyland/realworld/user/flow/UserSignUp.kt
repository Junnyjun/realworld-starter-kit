package io.junnyland.realworld.user.flow

import io.junnyland.realworld.user.action.out.repository.UserRepository
import io.junnyland.realworld.user.action.out.security.TokenProvider
import io.junnyland.realworld.user.action.out.security.TokenProvider.TokenRequest
import io.junnyland.realworld.user.domain.User
import org.springframework.stereotype.Service


interface UserSignUp {
    fun signUp(request: SignUpRequest): User

    @Service
    class UserSignUpUsecase(
        private val userRepository: UserRepository,
        private val tokenProvider: TokenProvider,
    ) : UserSignUp {
        val DEFAULT_AUTHORITY = listOf("ROLE_USER")

        override fun signUp(request: SignUpRequest): User = tokenProvider
            .generate(TokenRequest(request.username, DEFAULT_AUTHORITY))
            .let { userRepository.save(request.toDomain(it)) }
    }

    data class SignUpRequest(
        val email: String,
        val username: String,
        val password: String,
    ) {
        fun toDomain(token: String) = User(email, token, username, password, null, null)
    }
}

