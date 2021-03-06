package com.chattriggers.website

import io.javalin.core.JavalinConfig
import io.javalin.core.security.Role
import io.javalin.core.security.SecurityUtil.roles
import io.javalin.http.Context

object Auth {
    fun configure(config: JavalinConfig) {
        config.accessManager { handler, ctx, permittedRoles ->
            // If a route doesn't specify what roles can access it, assume everyone can.
            if (permittedRoles.isEmpty()) {
                handler.handle(ctx)
                return@accessManager
            }

            val role = getRoleForContext(ctx)

            if (role in permittedRoles) {
                handler.handle(ctx)
            } else {
                ctx.status(403).result("Forbidden")
            }
        }
    }

    private fun getRoleForContext(ctx: Context): Roles {
        return ctx.sessionAttribute<Roles>("role") ?: return Roles.default
    }

    enum class Roles : Role {
        admin, trusted, default
    }

    fun allRoles() = roles(Roles.default, Roles.trusted, Roles.admin)
    fun trustedOrHigher() = roles(Roles.trusted, Roles.admin)
    fun adminOnly() = roles(Roles.admin)
}