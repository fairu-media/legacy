package fairu.backend.utils.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTCreator
import com.auth0.jwt.algorithms.Algorithm
import fairu.backend.user.User
import fairu.backend.utils.Config
import naibu.ext.koin.get

object Token {
    private val algo: Algorithm by lazy {
        Algorithm.HMAC256(get<Config.Fairu>().jwt.secret)
    }

    const val audience = "users"
    const val issuer   = "fairu"

    val verifier = JWT.require(algo)
        .withAudience(audience)
        .withIssuer(issuer)
        .build()

    fun create(user: User, block: JWTCreator.Builder.() -> Unit): String = JWT.create()
        .withAudience(audience)
        .withIssuer(issuer)
        .withSubject(user.id.toString())
        .apply(block)
        .sign(algo)
}
