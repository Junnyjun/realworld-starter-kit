package io.junnyland.realworld.profile.action.out.repository.mongo

import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono

interface MongoProfileRepository: ReactiveMongoRepository<ProfileEntity, String> {
    fun existsByTargetAndFollower(target: String, follower: String): Mono<Boolean>
    fun deleteAllByTargetAndFollower(target: String, user: String) : Mono<Void>
}