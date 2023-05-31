package io.junnyland.realworld.user.flow

import io.junnyland.realworld.user.action.out.repository.UserRepository
import io.junnyland.realworld.user.action.out.security.TokenParser
import io.junnyland.realworld.user.action.out.security.TokenValidator
import io.junnyland.realworld.user.domain.User
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

interface FindUser {
    fun byToken(token: String): Mono<User>

    @Service
    class FindUserUsecase(
        private val userRepository: UserRepository,
        private val tokenParser: TokenParser
    ) : FindUser {
        override fun byToken(token: String) = token
            .removePrefix("Bearer ")
            .let { tokenParser.extract(it) }
            .let { it as UsernamePasswordAuthenticationToken }
            .let { userRepository.findBy(it.name) }
    }
}