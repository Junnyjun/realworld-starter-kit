package io.junnyland.realworld.user.action.`in`.http.model

import io.junnyland.realworld.user.flow.UserSignIn

data class RootRequest<T>(
    val user: T,
){
}

data class LoginRequest(
    val email: String,
    val password: String,
) {
    init {
        require(email.isNotBlank()) { "email must not be blank" }
        require(password.isNotBlank()) { "password must not be blank" }
    }
    val toUsecase get() = UserSignIn.SignInRequest(email, password)
}