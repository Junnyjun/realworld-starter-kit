package io.junnyland.realworld.profile.flow

import io.junnyland.realworld.profile.action.out.repository.ProfileRepository
import io.junnyland.realworld.profile.action.out.repository.mongo.ProfileEntity
import io.junnyland.realworld.profile.domain.Profile
import io.junnyland.realworld.user.action.out.repository.UserRepository
import io.junnyland.realworld.user.action.out.security.TokenParser
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.publisher.Mono.zip

interface FollowProfile {
    fun execute(request: Mono<FollowRequest>): Mono<ProfileEntity>

    @Service
    class FollowProfileUsecase(
        private val profile: ProfileRepository,
        private val user: UserRepository,
        private val tokenParser: TokenParser
        ) : FollowProfile {
        override fun execute(request: Mono<FollowRequest>)= request
                .doOnNext { user.exist(it.target) }
                .flatMap{ zip(user.findIdBy(tokenParser.extract(it.token).name), user.findIdByName(it.target)) }
                .flatMap { profile.follow(Profile(it.t1,it.t2)) }
        }


    data class FollowRequest(
        val token: String,
        val target: String
    )
}