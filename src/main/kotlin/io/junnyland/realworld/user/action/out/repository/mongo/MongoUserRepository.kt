package io.junnyland.realworld.user.action.out.repository.mongo

import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono

interface MongoUserRepository:ReactiveMongoRepository<UserEntity, String>{
    fun existsByEmail(email: String): Mono<Boolean>
    fun findByEmail(email: String): Mono<UserEntity>
    fun findByToken(token: String): Mono<UserEntity>
    fun findByUsername(name: String): Mono<UserEntity>
}