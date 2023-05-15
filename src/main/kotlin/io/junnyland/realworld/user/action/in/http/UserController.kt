package io.junnyland.realworld.user.action.`in`.http

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
        @RequestBody user: Mono<RootRequest>,
    ) =  user.map { it.user.toUsecase }
        .let { userSignIn.login(it) }



    data class RootRequest(
        val user: LoginRequest,
    )

    data class LoginRequest(
        val email: String,
        val password: String,
    ) {
        val toUsecase get() = SignInRequest(email, password)
    }
}