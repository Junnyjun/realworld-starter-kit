package io.junnyland.realworld.user.flow

import io.junnyland.realworld.user.action.out.repository.UserRepository
import io.junnyland.realworld.user.domain.User
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

interface UserSignIn {
    fun login(request: Mono<SignInRequest>): User

    @Service
    class UserSignInUsecase(
        private val userRepository: UserRepository,
    ) : UserSignIn {
        override fun login(request: Mono<SignInRequest>): User {
            return TODO()
        }

    }

    data class SignInRequest(
        private val email: String,
        private val username: String,
        private val password: String,
        private val bio: String?,
        private val image: String?,
    )
}