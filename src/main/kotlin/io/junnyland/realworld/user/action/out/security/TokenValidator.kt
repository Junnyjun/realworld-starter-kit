package io.junnyland.realworld.user.action.out.security

import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.junnyland.realworld.user.config.JwtConfig
import org.springframework.stereotype.Component


fun interface TokenValidator {
    fun by(token: String): Boolean

    @Component
    class JwtTokenValidator(
        private val securityInfo: JwtConfig.SecurityInfo,
    ) : TokenValidator {
        override fun by(token: String): Boolean = try {
            Jwts.parserBuilder()
                .setSigningKey(securityInfo.secretKey)
                .build()
                .parseClaimsJws(token)
            true
        } catch (jwtException: JwtException) {
            false
        } catch (exception: IllegalArgumentException) {
            false
        }
    }
}
