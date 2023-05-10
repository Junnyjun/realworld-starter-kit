package io.junnyland.realworld.user.action.out.repository

import io.junnyland.realworld.user.action.out.repository.mongo.MongoUserRepository
import io.junnyland.realworld.user.action.out.repository.mongo.UserEntity
import io.junnyland.realworld.user.domain.User
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

fun Boolean.isNotHave() = !this

interface UserRepository {
    fun save(user: User): Mono<User>

    @Repository
    class UserNosqlRepository(
        private val repository: MongoUserRepository,
    ) : UserRepository {
        override fun save(user: User) = repository.existsByEmail(user.email)
            .filter { it.isNotHave() }
            .flatMap { repository.save(UserEntity.byDomain(user)) }
            .map { it.toDomain }
            .doOnError { throw IllegalStateException("Create User Fail!") }
    }
}