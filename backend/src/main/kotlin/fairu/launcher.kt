package fairu

import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.createBucket
import aws.sdk.kotlin.services.s3.model.BucketCannedAcl
import com.akuleshov7.ktoml.file.TomlFileReader
import fairu.exception.RequestFailedException
import fairu.routes.files.files
import fairu.routes.image
import fairu.routes.session
import fairu.routes.users.users
import fairu.user.Session
import fairu.user.UserPrincipal
import fairu.user.access.AccessToken
import fairu.user.session.UserSession
import fairu.utils.BasicResponse
import fairu.utils.Config
import fairu.utils.Snowflake
import fairu.utils.Version
import fairu.utils.auth.Token
import fairu.utils.awt.registerFonts
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
import naibu.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.litote.kmongo.eq

val log by logging("fairu.application")
val configPath = System.getProperty("fairu.config-path") ?: "fairu.toml"

/**
 * Fairu application.
 *
 * ## Details
 * - Storage:  S3 via AWS-Kotlin-SDK
 * - Database: MongoDB
 * - Server:   Ktor
 *
 * @constructor Create empty Cdn
 */
suspend fun main() {
    log.info { "! Starting Fairu v${Version.FULL}" }

    /* load config */
    log.info { "* Loading configuration from '$configPath'" }
    val config = TomlFileReader.decodeFromFile(Config.serializer(), configPath).fairu

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

    // ensure configured bucket exists.
    val buckets = s3.listBuckets().buckets
        ?.map { it.name }
        ?: emptyList()

    if (config.s3.bucket !in buckets) {
        s3.createBucket {
            bucket = config.s3.bucket
            acl = BucketCannedAcl.PublicReadWrite
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
                loggerName = "fairu.server.requests"
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
                            .decodeToString()

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
