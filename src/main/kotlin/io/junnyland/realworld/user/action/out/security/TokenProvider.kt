package io.junnyland.realworld.user.action.out.security

import io.jsonwebtoken.Jwts
import io.junnyland.realworld.user.config.JwtConfig.SecurityInfo
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.time.Instant.now
import java.time.temporal.ChronoUnit.SECONDS
import java.util.Date.from

fun interface TokenProvider {
    fun generate(authentication: Authentication): String

    @Component
    class JwtTokenProvider(
        private val securityInfo: SecurityInfo,
    ) : TokenProvider {

        override fun generate(authentication: Authentication): String =Jwts.builder()
                .setClaims(authentication.toClaims())
                .setIssuer(securityInfo.issuer)
                .setIssuedAt(from(now()))
                .setExpiration(from(now().plus(securityInfo.expired, SECONDS)))
                .signWith(securityInfo.secretKey)
                .compact()
    }
}
private fun Authentication.toClaims() =  Jwts
    .claims()
    .setSubject(this.name)
    .also { it -> it["roles"] = this.authorities.joinToString(",") }