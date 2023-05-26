package io.junnyland.realworld.user.domain

import com.fasterxml.jackson.annotation.JsonIgnore

data class User(
    val email: String,
    val token: String = "NOT SET",
    val username: String,
    val password: String,
    val bio: String = "",
    val image: String = "",
) {

}
