package io.junnyland.realworld.global.security

import io.junnyland.realworld.user.action.out.security.TokenParser
import io.junnyland.realworld.user.action.out.security.TokenProvider.JwtTokenProvider
import io.junnyland.realworld.user.action.out.security.TokenValidator
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.FORBIDDEN
import org.springframework.http.HttpStatus.UNAUTHORIZED
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.access.PermissionEvaluator
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder.HTTP_BASIC
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity.ExceptionHandlingSpec
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono.fromRunnable
import reactor.core.publisher.Mono.just
import java.time.LocalDateTime


@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class SecurityConfig(
    private val applicationContext: ApplicationContext,
    private val tokenValidator: TokenValidator,
    private val tokenParser: TokenParser,
) {
    @Bean
    fun securityWebFilterChain(
        http: ServerHttpSecurity,
        jwtTokenProvider: JwtTokenProvider,
        reactiveAuthenticationManager: ReactiveAuthenticationManager,
    ): SecurityWebFilterChain = http
        .exceptionHandling {
            it
                .unauthorized()
                .accessDenied()
        }
        .cors().disable()
        .csrf().disable()
        .formLogin().disable()
        .httpBasic().disable()
        .authenticationManager(reactiveAuthenticationManager)
        .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
        .authorizeExchange { it
                .pathMatchers(HttpMethod.OPTIONS).permitAll()
                .pathMatchers(
                    HttpMethod.POST,
                    "/api/users/**",
                    "/api/users/login"
                ).permitAll()
                .pathMatchers(
                    HttpMethod.GET,
                    "/api/articles/{slug}/comments",
                    "/api/articles/{slug}",
                    "/api/articles",
                    "/api/profiles/{username}",
                    "/api/tags"
                ).permitAll()
                .anyExchange().authenticated()
        }
        .addFilterAt(JwtTokenAuthenticationFilter(tokenValidator, tokenParser), HTTP_BASIC)
        .also { defaultMethodSecurityExpressionHandler() }
        .build()

    @Bean
    fun jwtPermissionEvaluator(): PermissionEvaluator = JwtPermissionEvaluator()

    @Bean
    fun PasswordEncoder(): PasswordEncoder =
        PasswordEncoderFactories.createDelegatingPasswordEncoder()

    @Bean
    fun reactiveAuthenticationManager(
        userDetailsService: ReactiveUserDetailsService,
        passwordEncoder: PasswordEncoder,
    ): ReactiveAuthenticationManager =
        UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService)
            .also { it.setPasswordEncoder(passwordEncoder) }


    private fun defaultMethodSecurityExpressionHandler(): DefaultMethodSecurityExpressionHandler =
        this.applicationContext
            .getBean(DefaultMethodSecurityExpressionHandler::class.java)
            .also { it.setPermissionEvaluator(jwtPermissionEvaluator()) }


    private fun ExceptionHandlingSpec.unauthorized() =
        this.authenticationEntryPoint { exchange: ServerWebExchange, ex: AuthenticationException ->
            fromRunnable {
                exchange.response.statusCode = UNAUTHORIZED
                exchange.response.writeWith {
                    just(
                        exchange.response.bufferFactory().wrap(
                            ExceptionResponse(
                                ex.message!!,
                                UNAUTHORIZED
                            ).toDeserialize
                        )
                    )
                }
            }
        }

    private fun ExceptionHandlingSpec.accessDenied() =
        this.accessDeniedHandler { exchange: ServerWebExchange, denied: AccessDeniedException ->
            fromRunnable {
                exchange.response.statusCode = FORBIDDEN
                exchange.response.writeWith {
                    just(
                        exchange.response.bufferFactory()
                            .wrap(ExceptionResponse(denied.message!!, FORBIDDEN).toDeserialize)
                    )
                }
            }
        }

    private data class ExceptionResponse(
        val message: String,
        val status: HttpStatus,
        val timestamp: LocalDateTime = LocalDateTime.now(),
    ) {
        val toDeserialize: ByteArray
            get() = """ 
            {
            "message": "$message",
            "status": "${status.value()}",
            "timestamp": "$timestamp"
        }
        """.trimIndent().toByteArray()
    }
}