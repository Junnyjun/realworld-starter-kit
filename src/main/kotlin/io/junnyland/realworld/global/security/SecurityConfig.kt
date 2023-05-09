package io.junnyland.realworld.global.security

import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey

private fun corsConfiguration(): CorsConfiguration = CorsConfiguration()
    .also {
        it.setAllowedOriginPatterns(listOf("*"))
        it.allowedMethods = listOf("*")
        it.allowedHeaders = listOf("*")
        it.allowCredentials = true
    }

private fun resKey(publicKey: RSAPublicKey, privateKey: RSAPrivateKey) = RSAKey.Builder(publicKey)
    .privateKey(privateKey).build()


@Suppress("UNUSED_EXPRESSION")
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class SecurityConfig() {
    private val ketSet = KeyPairGenerator.getInstance("RSA")
        .also { it.initialize(2048) }
        .generateKeyPair()
        .let { it.public as RSAPublicKey to it.private as RSAPrivateKey }
//    @Bean
//    fun securityFilterChain(
//        http: HttpSecurity,
//        exceptionHandleFilter: ExceptionHandleFilter,
//    ): SecurityFilterChain =
//        http.httpBasic { obj: HttpBasicConfigurer<HttpSecurity> -> obj.disable() }
//            .csrf { obj: CsrfConfigurer<HttpSecurity> -> obj.disable() }
//            .formLogin { obj: FormLoginConfigurer<HttpSecurity> -> obj.disable() }
//            .cors { obj: CorsConfigurer<HttpSecurity> -> obj.and() }
//            .authorizeHttpRequests{ it
//                .requestMatchers(HttpMethod.POST, "/api/users", "/api/users/login").permitAll()
//                .requestMatchers(HttpMethod.GET, "/api/articles/{slug}/comments", "/api/articles/{slug}", "/api/articles", "/api/profiles/{username}", "/api/tags").permitAll()
//                .anyRequest().authenticated()
//            }
//            .oauth2ResourceServer(OAuth2ResourceServerConfigurer<HttpSecurity>::jwt)
//            .sessionManagement{ manager-> manager.sessionCreationPolicy(STATELESS) }
//            .exceptionHandling { it
//                .authenticationEntryPoint(BearerTokenAuthenticationEntryPoint())
//                .accessDeniedHandler(BearerTokenAccessDeniedHandler())
//            }
//            .build()

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun jwtDecoder(): JwtDecoder = NimbusJwtDecoder.withPublicKey(ketSet.first).build()

    @Bean
    fun jwtEncoder(): JwtEncoder =
        NimbusJwtEncoder(ImmutableJWKSet(JWKSet(resKey(ketSet.first, ketSet.second))))
}