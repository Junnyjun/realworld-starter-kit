package io.junnyland.realworld.user.action.out.repository.mongo

import io.junnyland.realworld.user.domain.User
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "User")
data class UserEntity(
    @Id private val id: ObjectId = ObjectId.get(),
    @Indexed(unique = true) private val email: String,
    private val token: String = "NOT_SET",
    private val username: String,
    private val password: String,
    private val bio: String?,
    private val image: String?,
    private val createdAt: LocalDateTime = LocalDateTime.now(),
) {
    fun entityId():String = id.toHexString()

    fun toDomain(): User = User(
        email = this.email,
        token = this.token,
        username = this.username,
        password = this.password,
        bio = this.bio,
        image = this.image,
    )

    companion object {
        fun byDomain(user: User): UserEntity = UserEntity(
            email = user.email,
            username = user.username,
            password = user.password,
            token = user.token,
            bio = user.bio,
            image = user.image,
        )

    }

}
