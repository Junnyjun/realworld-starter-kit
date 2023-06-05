package io.junnyland.realworld.profile.action.out.repository

import io.junnyland.realworld.profile.action.out.repository.mongo.MongoProfileRepository
import io.junnyland.realworld.profile.action.out.repository.mongo.ProfileEntity
import io.junnyland.realworld.profile.domain.Profile
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

interface ProfileRepository {
    fun isFollow(profile: Profile): Mono<Boolean>
    fun follow(profile: Profile): Mono<ProfileEntity>
    fun unfollow(profile: Profile): Mono<Void>

    @Repository
    class ProfileNosqlRepository(
        private val repository: MongoProfileRepository
    ) : ProfileRepository {

        override fun isFollow(profile: Profile): Mono<Boolean> =
            repository.existsByTargetAndFollower(profile.target, profile.user)

        override fun follow(profile: Profile) = repository.save(
            ProfileEntity(
                target = profile.target,
                follower = profile.user
            )
        )

        override fun unfollow(profile: Profile): Mono<
                Void> = repository.deleteAllByTargetAndFollower(
            profile.target,
            profile.user
        )
    }
}