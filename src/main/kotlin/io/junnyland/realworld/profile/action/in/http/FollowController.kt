package io.junnyland.realworld.profile.action.`in`.http

import io.junnyland.realworld.global.security.tokenPrefix
import io.junnyland.realworld.profile.flow.ProfileFollow
import io.junnyland.realworld.profile.flow.ProfileFollow.FollowRequest
import io.junnyland.realworld.profile.flow.ProfileUnFollow
import io.junnyland.realworld.profile.flow.ProfileUnFollow.UnFollowRequest
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/profiles")
class FollowController(
    private val profileFollow: ProfileFollow,
    private val profileUnFollow: ProfileUnFollow

) {

    @PostMapping("/{username}/follow")
    @ResponseStatus(HttpStatus.CREATED)
    fun follow(
        @RequestHeader("Authorization") token: String,
        @PathVariable("username") username: String
    ) = Mono
        .just(FollowRequest(token.tokenPrefix(), username))
        .let { profileFollow.execute(it) }

    @DeleteMapping("/{username}/follow")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun unfollow(
        @RequestHeader("Authorization") token: String,
        @PathVariable("username") username: String
    ) = Mono
        .just(UnFollowRequest(token.tokenPrefix(), username))
        .let { profileUnFollow.execute(it) }
}

