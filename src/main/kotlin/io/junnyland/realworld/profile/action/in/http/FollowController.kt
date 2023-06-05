package io.junnyland.realworld.profile.action.`in`.http

import io.junnyland.realworld.global.security.tokenPrefix
import io.junnyland.realworld.profile.flow.ProfileFollow
import io.junnyland.realworld.profile.flow.ProfileFollow.FollowRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/profiles")
class FollowController(
    private val profileFollow: ProfileFollow

) {

    @PostMapping("/{username}/follow")
    @ResponseStatus(HttpStatus.CREATED)
    fun follow(
        @RequestHeader("Authorization") token: String,
        @PathVariable("username") username: String
    ) = Mono
        .just(FollowRequest(token.tokenPrefix(), username))
        .let { profileFollow.execute(it) }
}

