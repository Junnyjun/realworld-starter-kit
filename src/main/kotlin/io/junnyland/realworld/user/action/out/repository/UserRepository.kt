package io.junnyland.realworld.user.action.out.repository

import io.junnyland.realworld.user.action.out.repository.mongo.MongoUserRepository
import io.junnyland.realworld.user.action.out.repository.mongo.UserEntity.Companion.byDomain
import io.junnyland.realworld.user.domain.User
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

private fun Boolean.isNotHave() = !this

interface UserRepository {
    fun save(user: User): Mono<User>
    fun findBy(email: String): Mono<User>
    fun parseBy(token: String): Mono<User>
    fun renewToken(email: String, token: String): Mono<User>
    fun fetch(email: String, target: User): Mono<User>
    fun exist(follower: String): Mono<Boolean>
    fun findIdBy(email: String) : Mono<String>
    fun findIdByName(target: String): Mono<String>

    @Repository
    class UserNosqlRepository(
        private val repository: MongoUserRepository,
        private val passwordEncoder: PasswordEncoder,
    ) : UserRepository {
        @Transactional
        override fun save(user: User) = repository.existsByEmail(user.email)
            .filter { it.isNotHave() }
            .flatMap { repository.save(byDomain(user, passwordEncoder.encode(user.password))) }
            .map { it.toDomain() }
            .doOnError { error("Create User Fail!") }

        @Transactional(readOnly = true)
        override fun findBy(email: String) = repository.findByEmail(email)
            .map { it.toDomain() }
            .doOnError { error("Find User Fail") }

        @Transactional(readOnly = true)
        override fun parseBy(token: String): Mono<User> = repository.findByToken(token)
            .map { it.toDomain() }
            .doOnError { error("Bad Token") }

        @Transactional
        override fun renewToken(email: String, token: String): Mono<User> = repository.findByEmail(email)
            .map { it.updateToken(token) }
            .flatMap { repository.save(it) }
            .map { it.toDomain() }
            .doOnError { error("Find User Fail") }

        override fun fetch(email: String, target: User) = repository.findByEmail(email)
            .map { it.update(target) }
            .doOnNext { if(target.password != "" ) { it.updatePassword(passwordEncoder.encode(target.password)) }}
            .flatMap { repository.save(it) }
            .map { it.toDomain() }
            .doOnError { error("Update User Fail") }

        @Transactional(readOnly = true)
        override fun exist(follower: String): Mono<Boolean> = repository.existsByEmail(follower)

        @Transactional(readOnly = true)
        override fun findIdBy(email: String): Mono<String> = repository.findByEmail(email)
        .map { it.entityId() }
        .doOnError { error("Find User Fail") }

        @Transactional(readOnly = true)
        override fun findIdByName(name: String): Mono<String> =repository.findByUsername(name)
            .map { it.entityId() }
            .doOnError { error("Find User Fail") }
    }

}
