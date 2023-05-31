package io.junnyland.realworld.user.action.`in`.http

import io.junnyland.realworld.user.action.`in`.http.model.*
import io.junnyland.realworld.user.flow.UserSignIn
import io.junnyland.realworld.user.flow.UserSignUp
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono


@RestController
@RequestMapping("/api/users")
class UserSigningController(
    private val signIn: UserSignIn,
    private val signUp: UserSignUp,
) {

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    fun login(
        @RequestBody user: Mono<RootRequest<LoginRequest>>,
    ): Mono<RootResponse> =  user
        .map { it.user.toUsecase }
        .let { signIn.login(it) }
        .map { UserResponse.from(it)  }
        .map { RootResponse(it) }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun signup(
        @RequestBody user: Mono<RootRequest<SignUpRequest>>,
    ): Mono<RootResponse> =  user
        .map { it.user.toUsecase }
        .let { signUp.signUp(it) }
        .map { UserResponse.from(it)  }
        .map { RootResponse(it) }
}