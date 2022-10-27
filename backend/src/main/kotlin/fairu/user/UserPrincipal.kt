package fairu.user

import fairu.user.access.AccessScope
import fairu.user.access.AccessToken
import fairu.user.session.UserSession
import io.ktor.server.auth.*

sealed class UserPrincipal(val user: User) : Principal {
    abstract fun canAccess(scope: AccessScope): Boolean

    class Session(user: User, val session: UserSession) : UserPrincipal(user) {
        /** User sessions can access any route. */
        override fun canAccess(scope: AccessScope): Boolean = true
    }

    class Token(user: User, val token: AccessToken) : UserPrincipal(user) {
        override fun canAccess(scope: AccessScope): Boolean = scope in token.scopes
    }
}
