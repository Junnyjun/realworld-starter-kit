package io.junnyland.realworld.user.action.out.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.junnyland.realworld.user.config.JwtConfig
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Component
import java.security.Key

fun interface TokenParser {
    fun extract(token: String): Authentication

    @Component
    class JwtTokenParser(
        private val securityInfo: JwtConfig.SecurityInfo,
    ) : TokenParser {
        override fun extract(token: String): Authentication = token.parseToken(securityInfo.secretKey)
                .let { it to it["roles"] as List<SimpleGrantedAuthority> }
                .let { userInfo: UserInfo -> User(userInfo.subject, "", userInfo.authorities) }
                .let { user: User -> UsernamePasswordAuthenticationToken(user, token, user.authorities) }
    }
}

private typealias UserInfo = Pair<Claims, List<SimpleGrantedAuthority>>

private val UserInfo.subject get() = this.first.subject
private val UserInfo.authorities get() = this.second

private fun String.parseToken(secretKey: Key) = Jwts.parserBuilder()
    .setSigningKey(secretKey)
    .build()
    .parseClaimsJws(this)
    .body