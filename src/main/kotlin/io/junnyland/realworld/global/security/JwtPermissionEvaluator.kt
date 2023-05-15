package io.junnyland.realworld.global.security

import org.springframework.security.access.PermissionEvaluator
import org.springframework.security.core.Authentication
import java.io.Serializable

class JwtPermissionEvaluator : PermissionEvaluator {
    override fun hasPermission(
        authentication: Authentication,
        targetDomainObject: Any,
        permission: Any,
    ) = authentication.authorities
        .stream()
        .filter { it.authority.equals(targetDomainObject) }
        .count() > 0

    override fun hasPermission(
        authentication: Authentication,
        targetId: Serializable,
        targetType: String,
        permission: Any,
    ) = false
}