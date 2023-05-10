package io.junnyland.realworld.user.action.`in`.http.handler

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.junnyland.realworld.global.Logger
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class AceessDeniedHandler
    : ServerAccessDeniedHandler {
    override fun handle(exchange: ServerWebExchange, denied: AccessDeniedException): Mono<Void> = exchange.response
            .also {
                it.setStatusCode(HttpStatus.UNAUTHORIZED)
                it.headers.contentType = MediaType.APPLICATION_JSON
            }
            .let {
                Logger.error("Unauthorized error: {}", denied.message!!)
                it.writeWith(Mono.just(it.bufferFactory().wrap(ErrorResponse(denied.message!!).toByteArray)))
            }

    data class ErrorResponse(
        val message: String
    ){
        val toByteArray: ByteArray get() = jacksonObjectMapper().writeValueAsBytes(this)
    }
}