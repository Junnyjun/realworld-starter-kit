package io.junnyland.realworld

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RealworldApplication

fun main(args: Array<String>) {
    runApplication<RealworldApplication>(*args)
}