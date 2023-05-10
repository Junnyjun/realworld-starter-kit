package io.junnyland.realworld.user.action.out.security

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.MACSigner
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

fun interface TokenProvider {
    fun generate(request: TokenRequest): String

    @Component
    class JwtTokenProvider : TokenProvider {
        override fun generate(request: TokenRequest): String = SignedJWT(JWSHeader(JWSAlgorithm.HS256), request.toClaim)
                .also { it.sign(MACSigner(SecurityProperties.SECRETS)) }
                .serialize()
    }

    data class TokenRequest(
        val username: String,
        val roles: List<String>,
    ){
        val toClaim: JWTClaimsSet
            get() =  JWTClaimsSet.Builder()
            .subject(username)
            .issuer(SecurityProperties.ISSUEER)
            .issueTime(Date.from(Instant.now()))
            .expirationTime(
                Date.from(
                    Instant.now().plus(SecurityProperties.EXPIRED, ChronoUnit.SECONDS)
                )
            )
            .claim("roles", roles)
            .build()
    }
}