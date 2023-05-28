package io.junnyland.realworld.user.action.out.repository

import io.junnyland.realworld.user.action.out.repository.mongo.MongoUserRepository
import io.junnyland.realworld.user.action.out.repository.mongo.UserEntity
import io.junnyland.realworld.user.domain.User
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

private fun Boolean.isNotHave() = !this

interface UserRepository {
    fun save(user: User): Mono<User>
    fun findBy(email: String): Mono<User>
    fun renewToken(email: String, token: String): Mono<User>

    @Repository
    class UserNosqlRepository(
        private val repository: MongoUserRepository,
        private val passwordEncoder: PasswordEncoder,
    ) : UserRepository {
        override fun save(user: User) = repository.existsByEmail(user.email)
            .filter { it.isNotHave() }
            .flatMap { repository.save(UserEntity.byDomain(user,passwordEncoder.encode(user.password))) }
            .map { it.toDomain() }
            .doOnError { throw IllegalStateException("Create User Fail!") }

        @Transactional(readOnly = true)
        override fun findBy(email: String) = repository.findByEmail(email)
            .map { it.toDomain() }
            .doOnError { throw UsernameNotFoundException("Find User Fail") }

        @Transactional
        override fun renewToken(email: String, token: String): Mono<User> =
            repository.findByEmail(email)
                .map { it.updateToken(token) }
                .flatMap { repository.save(it) }
                .map { it.toDomain() }
                .doOnError { throw UsernameNotFoundException("Find User Fail") }

    }
}
