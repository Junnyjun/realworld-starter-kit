package io.junnyland.realworld.user.action.out.security

import io.junnyland.realworld.user.action.out.repository.UserRepository
import io.junnyland.realworld.user.domain.User
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component


interface FindSecurityUser : ReactiveUserDetailsService {
    @Component
    class FindSecurityUserByDb(
        private val userRepository: UserRepository,
    ): FindSecurityUser {
        override fun findByUsername(username: String) = userRepository.findBy(username)
            .map { it.toSecurity() }
            .doOnError { throw UsernameNotFoundException("Find User Fail") }
    }
}
private fun User.toSecurity():UserDetails = org.springframework.security.core.userdetails.User(
    this.email,
    this.password,
    listOf(SimpleGrantedAuthority("ROLE_USER"))
)