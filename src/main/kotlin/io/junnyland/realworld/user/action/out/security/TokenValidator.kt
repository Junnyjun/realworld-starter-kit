package io.junnyland.realworld.user.action.out.security

import com.nimbusds.jose.crypto.MACVerifier
import com.nimbusds.jwt.SignedJWT
import org.springframework.stereotype.Component
import java.util.*

fun interface TokenValidator {
    fun by(token: String): Boolean

    @Component
    class JwtTokenValidator : TokenValidator {
        override fun by(token: String): Boolean = SignedJWT.parse(token)
            .let { signed ->
                signed.verify(MACVerifier(SecurityProperties.SECRETS)) &&
                signed.jwtClaimsSet.issuer == SecurityProperties.ISSUEER &&
                signed.jwtClaimsSet.expirationTime.after(Date())
            }

    }
}
