package io.junnyland.realworld.user.action.`in`.http

import io.junnyland.realworld.user.action.`in`.http.model.RootResponse
import io.junnyland.realworld.user.action.`in`.http.model.UserResponse
import io.junnyland.realworld.user.flow.FindUser
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/user")
class UserHttpController(
    private val find: FindUser,
) {
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    suspend fun find(
        @RequestHeader("Authorization") token: String,
    ): RootResponse? = find.byToken(token)
        .map { UserResponse.from(it) }
        .map { RootResponse(it) }
        .awaitSingle()
        ?: throw Exception("User not found")


}