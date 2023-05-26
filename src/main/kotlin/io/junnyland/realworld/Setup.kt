package io.junnyland.realworld

import io.junnyland.realworld.user.action.out.repository.UserRepository
import io.junnyland.realworld.user.domain.User
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class Setup (
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
):CommandLineRunner{
    override fun run(vararg args: String?) {
        val save = userRepository.save(
            User(
                email = "jake@jake.jake",
                password = passwordEncoder.encode("1234"),
                username = "jake",
                bio = "I work at statefarm"
            )
        )
        val block = save.block()
    }
}