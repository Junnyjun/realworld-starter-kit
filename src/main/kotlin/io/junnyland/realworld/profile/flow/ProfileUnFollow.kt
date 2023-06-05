package io.junnyland.realworld.profile.flow

import io.junnyland.realworld.profile.action.out.repository.ProfileRepository
import io.junnyland.realworld.profile.action.out.repository.mongo.ProfileEntity
import io.junnyland.realworld.profile.domain.Profile
import io.junnyland.realworld.user.action.out.repository.UserRepository
import io.junnyland.realworld.user.action.out.security.TokenParser
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono


fun interface ProfileUnFollow {
    fun execute(request: Mono<UnFollowRequest>): Mono<Void>

    @Service
    class ProfileUnFollowUsecase(
        private val profile: ProfileRepository,
        private val user: UserRepository,
        private val tokenParser: TokenParser
    ) : ProfileUnFollow {
        override fun execute(request: Mono<UnFollowRequest>): Mono<Void> = request
            .doOnNext { user.exist(it.target) }
            .flatMap{ Mono.zip(user.findIdBy(tokenParser.extract(it.token).name), user.findIdByName(it.target)) }
            .flatMap { profile.unfollow(Profile(it.t1,it.t2)) }
    }


    data class UnFollowRequest(
        val token: String,
        val target: String
    )
}