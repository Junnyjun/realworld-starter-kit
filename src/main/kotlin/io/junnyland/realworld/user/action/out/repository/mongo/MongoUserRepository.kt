package io.junnyland.realworld.user.action.out.repository.mongo

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono

interface MongoUserRepository:MongoRepository<UserEntity, String>{
    fun existsByEmail(email: String): Boolean
}