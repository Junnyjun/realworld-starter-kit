package io.junnyland.realworld.profile.action.out.repository.mongo

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "Profile")
data class ProfileEntity(
    @Id private val id: ObjectId = ObjectId.get(),
    @Indexed(unique = true) private val target: String,
    @Indexed(unique = true) private val follower: String,
    private val createdAt: LocalDateTime = LocalDateTime.now(),
) {
}