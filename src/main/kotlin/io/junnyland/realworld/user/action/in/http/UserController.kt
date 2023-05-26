package io.junnyland.realworld.user.action.`in`.http

import io.junnyland.realworld.user.action.`in`.http.model.LoginRequest
import io.junnyland.realworld.user.action.`in`.http.model.RootRequest
import io.junnyland.realworld.user.action.`in`.http.model.RootResponse
import io.junnyland.realworld.user.action.`in`.http.model.UserResponse
import io.junnyland.realworld.user.domain.User
import io.junnyland.realworld.user.flow.UserSignIn
import io.junnyland.realworld.user.flow.UserSignIn.SignInRequest
import org.springframework.context.ApplicationContext
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono


@RestController
@RequestMapping("/api/users")
class UserSignInHttpController(
    private val userSignIn: UserSignIn,
) {

    @PostMapping("/login")
    fun login(
        @RequestBody user: Mono<RootRequest<LoginRequest>>,
    ): Mono<RootResponse> =  user
        .map { it.user.toUsecase }
        .let { userSignIn.login(it) }
        .map { UserResponse.from(it)  }
        .map { RootResponse(it) }
}