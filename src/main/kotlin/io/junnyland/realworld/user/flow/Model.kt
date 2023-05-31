package io.junnyland.realworld.user.flow

sealed interface Model {

}

typealias UserAndToken = Pair<String, String>

val UserAndToken.name: String get() = this.first
val UserAndToken.token get() = this.second