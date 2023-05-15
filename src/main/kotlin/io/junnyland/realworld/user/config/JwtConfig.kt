package io.junnyland.realworld.user.config

import io.jsonwebtoken.security.Keys
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import java.util.*

@Configuration
class JwtConfig {

    @Bean(name = ["securityInfo"])
    @Profile("live")
    fun live() = SecurityInfo(
        "realworldlive",
        "junnyland",
        60 * 60
    )

    @Bean(name = ["securityInfo"])
    fun default() = SecurityInfo(
        "realworld",
        "junnyland",
        60 * 60
    )

    data class SecurityInfo(
        val secret: String,
        val issuer: String,
        val expired: Long,
    ){
        val secretKey get() = Keys.hmacShaKeyFor(Base64.getEncoder().encode(secret.toByteArray()));
    }
}