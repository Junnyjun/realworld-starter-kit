package io.junnyland.realworld.user.action.`in`.http.security

import io.junnyland.realworld.user.action.`in`.http.handler.AceessDeniedHandler
import io.junnyland.realworld.user.action.out.security.TokenParser
import io.junnyland.realworld.user.action.out.security.TokenValidator
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
    private val parser: TokenParser,
    private val validate: TokenValidator,
    private val aceessDeniedHandler: AceessDeniedHandler,
) {
    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain? {
        return http.csrf().disable()
            .authorizeExchange()
            .pathMatchers("/api/**", "/api/users/login").permitAll()
            .anyExchange().authenticated()
            .and()
            .addFilterAt(
                JwtAuthenticationFilter(parser,validate, reactiveAuthenticationManager(),authenticationConverter()),
                SecurityWebFiltersOrder.AUTHENTICATION
            )
            .exceptionHandling {
                it.accessDeniedHandler(aceessDeniedHandler)
            }
            .build()
    }

    @Bean
    fun reactiveAuthenticationManager(): ReactiveAuthenticationManager =
        JwtAuthenticationManager(parser, validate)
    @Bean
    fun authenticationConverter(): AuthenticationConverter =
        AuthenticationConverter(parser, validate)
}