package io.junnyland.realworld

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RealworldApplication

fun main(args: Array<String>) {
    runApplication<RealworldApplication>(*args)
}
