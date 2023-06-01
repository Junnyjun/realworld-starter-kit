package io.junnyland.realworld.profile.action.out.repository.mongo

import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface MongoProfileRepository: ReactiveMongoRepository<ProfileEntity, String> {
    fun existsByTargetAndFollower(target: String, follower: String): Boolean
}