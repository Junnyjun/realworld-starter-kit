package io.junnyland.realworld.global.security

import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity.AuthorizeExchangeSpec
import org.springframework.security.config.web.server.ServerHttpSecurity.ExceptionHandlingSpec
import org.springframework.security.core.AuthenticationException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
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

private val ketSet = KeyPairGenerator.getInstance("RSA")
    .also { it.initialize(2048) }
    .generateKeyPair()
    .let { it.public as RSAPublicKey to it.private as RSAPrivateKey }

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class SecurityConfig(
    private val applicationContext: ApplicationContext
) {


    @Bean
    @DependsOn("methodSecurityExpressionHandler")
    fun securityWebFilterChain(
        http: ServerHttpSecurity,
        jwtTokenProvider: JwtTokenProvider,
        reactiveAuthenticationManager: ReactiveAuthenticationManager,
    ): SecurityWebFilterChain? {
        val defaultWebSecurityExpressionHandler: DefaultMethodSecurityExpressionHandler =
            this.applicationContext.getBean(
                DefaultMethodSecurityExpressionHandler::class.java
            )
        defaultWebSecurityExpressionHandler.setPermissionEvaluator(myPermissionEvaluator())
        return http
            .exceptionHandling { exceptionHandlingSpec: ExceptionHandlingSpec ->
                exceptionHandlingSpec
                    .authenticationEntryPoint { exchange: ServerWebExchange, ex: AuthenticationException? ->
                        Mono.fromRunnable {
                            exchange.response.setStatusCode(HttpStatus.UNAUTHORIZED)
                        }
                    }
                    .accessDeniedHandler { exchange: ServerWebExchange, denied: AccessDeniedException? ->
                        Mono.fromRunnable {
                            exchange.response.setStatusCode(HttpStatus.FORBIDDEN)
                        }
                    }
            }
            .csrf().disable()
            .formLogin().disable()
            .httpBasic().disable()
            .authenticationManager(reactiveAuthenticationManager)
            .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
            .authorizeExchange { exchange: AuthorizeExchangeSpec ->
                exchange
                    .pathMatchers(HttpMethod.OPTIONS).permitAll()
                    .pathMatchers("/login").permitAll()
                    .anyExchange().authenticated()
            }
            .addFilterAt(
                JwtTokenAuthenticationFilter(jwtTokenProvider),
                SecurityWebFiltersOrder.HTTP_BASIC
            )
            .build()
    }


    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun jwtDecoder(): JwtDecoder = NimbusJwtDecoder.withPublicKey(ketSet.first).build()

    @Bean
    fun jwtEncoder(): JwtEncoder =
        NimbusJwtEncoder(ImmutableJWKSet(JWKSet(resKey(ketSet.first, ketSet.second))))
}