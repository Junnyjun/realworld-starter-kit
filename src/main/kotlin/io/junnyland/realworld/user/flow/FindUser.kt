package io.junnyland.realworld.user.flow

import io.junnyland.realworld.user.action.out.repository.UserRepository
import io.junnyland.realworld.user.domain.User
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

interface FindUser {
   fun byEmail(email: String): Mono<User>

   @Service
   class FindUserUsecase(
       private val userRepository: UserRepository,
   ) : FindUser {
      override fun byEmail(email: String) = userRepository.findBy(email)
   }
}