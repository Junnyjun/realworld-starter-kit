package io.junnyland.realworld.global.security

import com.nimbusds.jose.JWSAlgorithm.HS256
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.MACSigner
import com.nimbusds.jose.crypto.MACVerifier
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Component
import java.time.Instant.now
import java.time.temporal.ChronoUnit.SECONDS
import java.util.*
import java.util.Date.from
import java.util.stream.Collectors


const val ISSUEER = "junnyland"
const val SECRETS = "realworld"
const val EXPIRED = 2000L


@Component
class JwtTokenProvider {
    fun createToken(username: String, roles: List<String>): String =
        SignedJWT(JWSHeader(HS256), claimsSet(username, roles))
            .also { it.sign(MACSigner(SECRETS)) }
            .serialize()

    fun validateToken(token: String): Boolean = SignedJWT.parse(token)
        .let { signed ->
            signed.verify(MACVerifier(SECRETS)) &&
                    signed.jwtClaimsSet.issuer == ISSUEER &&
                    signed.jwtClaimsSet.expirationTime.after(Date())
        }

    fun getAuthentication(token: String): Authentication = parseToken(token)
        .let { userInfo: UsenameWithAuthorites -> User(userInfo.usename, "", userInfo.authorities) }
        .let { UsernamePasswordAuthenticationToken(it, "", it.authorities) }
}

private fun claimsSet(username: String, roles: List<String>) = JWTClaimsSet.Builder()
    .subject(username)
    .issuer(ISSUEER)
    .issueTime(from(now()))
    .expirationTime(from(now().plus(EXPIRED, SECONDS)))
    .claim("roles", roles)
    .build()

private fun parseToken(token: String): UsenameWithAuthorites = SignedJWT.parse(token)
    .let { signed ->
        signed.jwtClaimsSet.subject to
                signed.jwtClaimsSet.getClaim("roles")
                    .let { it as List<String> }
                    .stream()
                    .map { role -> SimpleGrantedAuthority(role) }
                    .collect(Collectors.toList())
    }

private typealias UsenameWithAuthorites = Pair<String, List<SimpleGrantedAuthority>>

private val UsenameWithAuthorites.usename: String get() = this.first
private val UsenameWithAuthorites.authorities: List<SimpleGrantedAuthority> get() = this.second