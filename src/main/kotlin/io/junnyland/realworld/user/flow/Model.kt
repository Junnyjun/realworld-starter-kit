package io.junnyland.realworld.user.flow

typealias UserAndToken = Pair<String, String>

val UserAndToken.name: String get() = this.first
val UserAndToken.token get() = this.second