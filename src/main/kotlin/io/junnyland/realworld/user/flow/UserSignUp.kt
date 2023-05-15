//package io.junnyland.realworld.user.flow
//
//import io.junnyland.realworld.user.action.out.repository.UserRepository
//import io.junnyland.realworld.user.action.out.security.TokenProvider
//import io.junnyland.realworld.user.domain.User
//import org.springframework.stereotype.Service
//import reactor.core.publisher.Mono
//import reactor.core.publisher.Mono.zip
//import reactor.util.function.Tuple2
//
//
//interface UserSignUp {
//    fun signUp(request: Mono<SignUpRequest>): Mono<User>
//
//    @Service
//    class UserSignUpUsecase(
//        private val userRepository: UserRepository,
//        private val tokenProvider: TokenProvider,
//    ) : UserSignUp {
//        private val DEFAULT_AUTHORITY = listOf("ROLE_USER")
//
//        override fun signUp(request: Mono<SignUpRequest>): Mono<User> =
//            request
//                .flatMap { userRepository.findBy(it.email) }
//
//
//        private fun toToken(request: Mono<SignUpRequest>) = request
//            .map { tokenProvider.generate(TokenRequest(it.username, DEFAULT_AUTHORITY)) }
//    }
//
//    data class SignUpRequest(
//        val email: String,
//        val password: String,
//    ) {
//    }
//}


