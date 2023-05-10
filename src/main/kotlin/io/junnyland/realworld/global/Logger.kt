package io.junnyland.realworld.global

import io.junnyland.realworld.RealworldApplication
import org.slf4j.LoggerFactory

object Logger {
    val logger = LoggerFactory.getLogger(RealworldApplication::class.java)

    fun info(message: String, vararg info: String) = logger.info(message,info)
    fun debug(message: String, vararg info: String) = logger.debug(message,info)
    fun warn(message: String, vararg info: String) = logger.warn(message,info)
    fun error(message: String, vararg info: String) = logger.error(message,info)
}