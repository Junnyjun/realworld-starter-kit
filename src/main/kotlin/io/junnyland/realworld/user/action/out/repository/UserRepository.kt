package io.junnyland.realworld.user.action.out.repository

import io.junnyland.realworld.user.action.out.repository.mongo.MongoUserRepository
import io.junnyland.realworld.user.action.out.repository.mongo.UserEntity
import io.junnyland.realworld.user.domain.User
import org.springframework.stereotype.Repository

fun Boolean.isNotHave() = !this

interface UserRepository {
    fun save(user: User): User

    @Repository
    class UserNosqlRepository(
        private val repository: MongoUserRepository,
    ) : UserRepository {
        override fun save(user: User) = takeUnless { repository.existsByEmail(user.email) }
                ?.let { repository.save(UserEntity.byDomain(user)) }
                ?.toDomain
                ?: throw IllegalArgumentException("이미 있는 유저")
    }
}