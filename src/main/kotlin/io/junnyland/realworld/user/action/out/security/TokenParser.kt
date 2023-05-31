package io.junnyland.realworld.user.action.out.security

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
            .let {  User(it.subject, "", listOf(SimpleGrantedAuthority(it["roles"].toString()))) }
            .let { user: User -> UsernamePasswordAuthenticationToken(user, token, user.authorities) }
    }
}
private fun String.parseToken(secretKey: Key) = Jwts.parserBuilder()
    .setSigningKey(secretKey)
    .build()
    .parseClaimsJws(this)
    .body