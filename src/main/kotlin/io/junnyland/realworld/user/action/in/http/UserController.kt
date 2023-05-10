package io.junnyland.realworld.user.action.`in`.http

import com.fasterxml.jackson.annotation.JsonRootName
import io.junnyland.realworld.user.flow.UserSignUp
import io.junnyland.realworld.user.flow.UserSignUp.SignUpRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono


@RestController
@RequestMapping("/api/users")
class UserSignInHttpController(
    private val userSignUp: UserSignUp,
) {

    @PostMapping("/login")
    fun login(
        @RequestBody user: LoginRequest,
    ) {

    }

    @PostMapping
    fun register(
        @RequestBody user: Mono<SignUpHttpRequest>,

        ) = user
        .map { it.toUsecase }
        .let { userSignUp.signUp(it) }
}

data class LoginRequest(
    private val email: String,
    private val password: String,
)

@JsonRootName("user")
data class SignUpHttpRequest(
    private val username: String,
    private val email: String,
    private val password: String,
) {
    val toUsecase get() = SignUpRequest(email, username, password)
}



