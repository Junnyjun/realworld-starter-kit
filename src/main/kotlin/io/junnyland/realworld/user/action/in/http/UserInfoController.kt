package io.junnyland.realworld.user.action.`in`.http

import io.junnyland.realworld.user.action.`in`.http.model.RootRequest
import io.junnyland.realworld.user.action.`in`.http.model.RootResponse
import io.junnyland.realworld.user.action.`in`.http.model.UpdateRequest
import io.junnyland.realworld.user.action.`in`.http.model.UserResponse
import io.junnyland.realworld.user.flow.UserFind
import io.junnyland.realworld.user.flow.UserUpdate
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/user")
class UserInfoController(
    private val find: UserFind,
    private val update: UserUpdate
) {
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    suspend fun find(
        @RequestHeader("Authorization") token: String,
    ): RootResponse? = find.byToken(token)
        .map { UserResponse.from(it) }
        .map { RootResponse(it) }
        .awaitSingle()
        ?: error("User not found")

    @PutMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun update(
        @RequestHeader("Authorization") token: String,
        @RequestBody request: Mono<RootRequest<UpdateRequest>>,
    ): Mono<RootResponse> = request
        .map { it.user.toDomain(token) }
        .let { update.fetch(it) }
        .map { UserResponse.from(it) }
        .map { RootResponse(it) }

}