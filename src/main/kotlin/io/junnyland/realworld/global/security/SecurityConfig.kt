package io.junnyland.realworld.global.security

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain


@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class SecurityConfig(
    private val jwtTokenProvider: JwtTokenProvider,
    private val aceessDeniedHandler: AceessDeniedHandler
) {
    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain? {
        return http.csrf().disable()
            .authorizeExchange()
            .pathMatchers("/api/**", "/api/users/login").permitAll()
            .anyExchange().authenticated()
            .and()
            .addFilterAt(
                JwtAuthenticationFilter(jwtTokenProvider,reactiveAuthenticationManager()),
                SecurityWebFiltersOrder.AUTHENTICATION
            )
            .exceptionHandling{
                it.accessDeniedHandler(aceessDeniedHandler)

            }
            .build()
    }

    @Bean
    fun reactiveAuthenticationManager(): ReactiveAuthenticationManager {
        return JwtAuthenticationManager(jwtTokenProvider)
    }
}