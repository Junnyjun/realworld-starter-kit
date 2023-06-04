package io.junnyland.realworld.profile.action.`in`.http

import io.junnyland.realworld.global.security.tokenPrefix
import io.junnyland.realworld.profile.flow.FollowProfile
import io.junnyland.realworld.profile.flow.FollowProfile.FollowRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/profiles")
class FollowController(
    private val followProfile: FollowProfile

) {

    @PostMapping("/{username}/follow")
    @ResponseStatus(HttpStatus.CREATED)
    fun follow(
        @RequestHeader("Authorization") token: String,
        @PathVariable("username") username: String
    ) = Mono
        .just(FollowRequest(token.tokenPrefix(), username))
        .let { followProfile.execute(it) }
}

