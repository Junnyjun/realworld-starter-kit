package io.junnyland.realworld.user.action.out.security

import com.nimbusds.jwt.SignedJWT
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.util.stream.Collectors

private typealias UsenameWithAuthorites = Pair<String, List<SimpleGrantedAuthority>>

private val UsenameWithAuthorites.toUser: User get() = User(this.first, "", this.second)

fun interface TokenParser {
    fun extract(token: String): Authentication

    @Component
    class JwtTokenParser : TokenParser {
        override fun extract(token: String): Authentication = parseToken(token)
            .let { userInfo: UsenameWithAuthorites -> userInfo.toUser }
            .let { UsernamePasswordAuthenticationToken(it, "", it.authorities) }

        private fun parseToken(token: String): UsenameWithAuthorites = SignedJWT.parse(token)
            .let { signed ->
                signed.jwtClaimsSet.subject to signed.jwtClaimsSet.getClaim("roles")
                    .let { it as List<String> }
                    .stream()
                    .map { role -> SimpleGrantedAuthority(role) }
                    .collect(Collectors.toList())
            }
    }
}