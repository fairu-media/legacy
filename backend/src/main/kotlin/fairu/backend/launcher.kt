package fairu.backend

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.createBucket
import com.akuleshov7.ktoml.Toml
import fairu.backend.exception.RequestFailedException
import fairu.backend.routes.files.files
import fairu.backend.routes.image
import fairu.backend.routes.session
import fairu.backend.routes.users.users
import fairu.backend.user.Session
import fairu.backend.user.UserPrincipal
import fairu.backend.user.access.AccessToken
import fairu.backend.user.session.UserSession
import fairu.backend.utils.*
import fairu.backend.utils.auth.Token
import fairu.backend.utils.awt.registerFonts
import fairu.frontend.routes.index
import fairu.frontend.routes.isFrontend
import fairu.frontend.routes.notFound
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.cio.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import naibu.encoding.Base64
import naibu.encoding.decode
import naibu.encoding.encode
import naibu.ext.koin.inject
import naibu.ext.ktor.server.plugins.logging.RequestLogging
import naibu.ext.ktor.server.setupServer
import naibu.io.order.BigEndian
import naibu.io.order.getULong
import naibu.io.slice.asSlice
import naibu.io.toByteArray
import naibu.logging.logging
import naibu.math.toIntSafe
import naibu.monads.expect
import naibu.serialization.DefaultFormats
import naibu.serialization.deserialize
import naibu.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.litote.kmongo.eq
import kotlin.io.path.Path
import kotlin.io.path.readText

val log by logging("fairu.backend.application")
val configPath = System.getProperty("fairu.backend.config-path") ?: "fairu.toml"

suspend fun main() {
    log.info { "! Starting Fairu v${Version.FULL}" }

    /* load config */
    log.info { "* Loading configuration from '$configPath'" }
    val config = Path(configPath)
        .readText()
        .deserialize<Config>(Toml)
        .fairu

    /* initialize dependency injection */
    startKoin {
        modules(module {
            single { config }
        })

        modules(fairuModule)
    }

    /* initialize storage trailer */
    log.info { "* Creating S3 bucket '${config.s3.bucket}'" }
    val s3 by inject<S3Client>()

    val buckets = s3.listBuckets().buckets
        ?.map { it.name }
        ?: emptyList()

    if (config.s3.bucket !in buckets) {
        s3.createBucket {
            bucket = config.s3.bucket

            createBucketConfiguration {
                locationConstraint = config.s3.region
            }
        }
    }

    /* load fonts */
    registerFonts()

    /* setup server */
    val server = setupServer(CIO) {
        production = true

        host = config.server.host
        port = config.server.port.toIntSafe()

        server {
            install(RequestLogging) {
                loggerName = "fairu.backend.server.requests"
            }

            install(CORS) {
                allowMethod(HttpMethod.Get)
                allowMethod(HttpMethod.Put)
                allowMethod(HttpMethod.Post)
                allowMethod(HttpMethod.Head)
                allowMethod(HttpMethod.Patch)
                allowMethod(HttpMethod.Delete)
                allowMethod(HttpMethod.Options)

                anyHost()

                allowNonSimpleContentTypes = true
                allowCredentials = true
            }

            install(ContentNegotiation) {
                json(DefaultFormats.Json)
            }

            install(StatusPages) {
                exception<RequestFailedException> { call, cause ->
                    call.respond(
                        cause.statusCode,
                        BasicResponse(mapOf("message" to cause.message), false)
                    )
                }

                status(HttpStatusCode.NotFound) { call, status ->
                    if (call.isFrontend) {
                        call.notFound()
                    } else {
                        val response = BasicResponse(Message("${call.request.path()} was not found!"), false)
                        call.respond(status, response)
                    }
                }
            }

            install(DefaultHeaders) {
                header("Server", "Fairu")

                header("X-Powered-By", "catboys <3")
                header("X-Fairu-Version", Version.FULL)
            }

            install(Sessions) {
                cookie<Session>("fairu_session") {
                    serializer = object : SessionSerializer<Session> {
                        override fun serialize(session: Session): String = session.id.value
                            .toByteArray()
                            .encode(Base64)

                        override fun deserialize(text: String): Session {
                            val id = BigEndian.getULong(
                                text.decode(Base64)
                                    .expect("Couldn't decode base64 session bytes")
                                    .asSlice()
                            )

                            return Session(Snowflake(id))
                        }
                    }
                }
            }

            authentication {
                session<Session>("session") {
                    validate { session ->
                        UserSession.get(session.id)?.let {
                            val user = it.user()!!
                            UserPrincipal.Session(user, it)
                        }
                    }

                    challenge {
                        call.sessions.clear<Session>()
                        call.respond(HttpStatusCode.Unauthorized)
                    }
                }

                jwt("access_token") {
                    realm = "fairu"

                    verifier(Token.verifier)

                    validate { cred ->
                        val tokenId = cred.payload.claims["token_id"]
                            ?.asString()
                            ?.let { Snowflake(it) }

                        if (cred.subject.isNullOrBlank() || tokenId == null) {
                            null
                        } else {
                            val token = AccessToken.find(AccessToken::id eq tokenId)
                                ?: return@validate null

                            UserPrincipal.Token(token.user()!!, token)
                        }
                    }

                    challenge { _, _ ->
                        call.respond(HttpStatusCode.Unauthorized)
                    }
                }
            }

            routing {
                // frontend
                index()

                // api
                route("/v1") {
                    // /login
                    session()

                    // /users
                    users()

                    // files
                    files()
                }

                get("/") {
                    call.respond(mapOf("message" to "Welcome to Fairu!", "version" to Version.FULL))
                }

                // get file
                image()
            }
        }
    }

    server.start(wait = true)
}
