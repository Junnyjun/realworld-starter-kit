package io.junnyland.realworld.user.action.out.repository

import io.junnyland.realworld.user.action.out.repository.mongo.MongoUserRepository
import io.junnyland.realworld.user.action.out.repository.mongo.UserEntity
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import reactor.test.StepVerifier

@DataMongoTest
class UserRepositoryTest{
    @Autowired
    lateinit var mongoRepository: MongoUserRepository

    @Test
    fun save(){
        val user = UserEntity(
            email = "test@mail.com",
            username = "test",
            password = "password",
            bio = "bio",
            image = "image",
        )
        ;
        val save = mongoRepository.save(user)
        StepVerifier.create(save)
            .assertNext {
                println(it.toDomain())
                assertNotNull(it.entityId())
            }
            .verifyComplete()

    }
}