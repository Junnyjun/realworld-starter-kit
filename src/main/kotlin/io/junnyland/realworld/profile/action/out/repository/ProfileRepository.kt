package io.junnyland.realworld.profile.action.out.repository

interface ProfileRepository {
    fun isFollow(target: String, follower: String): Boolean


    class ProfileNosqlRepository: ProfileRepository {

        override fun isFollow(target: String, follower: String): Boolean {
            TODO("Not yet implemented")
        }
    }
}