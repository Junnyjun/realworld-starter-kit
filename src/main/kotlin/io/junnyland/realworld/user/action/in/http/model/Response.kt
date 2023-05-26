package io.junnyland.realworld.user.action.`in`.http.model

import io.junnyland.realworld.user.domain.User

data class RootResponse(
    val user: UserResponse,
)

data class UserResponse(
    val email: String,
    val token: String,
    val username: String,
    val bio: String,
    val image: String,
) {
    companion object {
        fun from(user: User) = UserResponse(
            email = user.email,
            token = user.token,
            username = user.username,
            bio = user.bio,
            image = user.image,
        )
    }
}
