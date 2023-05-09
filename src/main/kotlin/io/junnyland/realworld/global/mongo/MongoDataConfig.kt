package io.junnyland.realworld.global.mongo

import de.flapdoodle.embed.mongo.MongodStarter
import de.flapdoodle.embed.mongo.config.MongodConfig
import de.flapdoodle.embed.mongo.config.Net
import de.flapdoodle.embed.mongo.distribution.Version
import de.flapdoodle.embed.process.runtime.Network
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order


@Configuration
open class MongoDataConfig(
    @Value("\${spring.data.mongodb.host}") val host: String,
    @Value("\${spring.data.mongodb.port}") val port: Int,
) {
    val mongodConfig: MongodConfig = MongodConfig
        .builder()
        .version(Version.Main.PRODUCTION)
        .net(Net(host, port, Network.localhostIsIPv6()))
        .build()

    @Bean
    @Order(1)
    fun connectEmbeddedMongodb() {
        MongodStarter.getDefaultInstance()
            .prepare(mongodConfig)
            .start()
    }
}