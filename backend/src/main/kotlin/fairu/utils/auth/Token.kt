package fairu.utils.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import fairu.mongo.User
import fairu.utils.Config
import fairu.utils.ext.Date
import naibu.ext.koin.get
import naibu.time.now
import kotlin.time.Duration.Companion.days

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

    fun create(user: User): String = JWT.create()
        .withAudience(audience)
        .withIssuer(issuer)
        .withSubject(user.id.toString())
        .withExpiresAt(Date(now() + 7.days))
        .sign(algo)
}
