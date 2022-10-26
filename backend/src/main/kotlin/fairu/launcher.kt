package fairu

import com.akuleshov7.ktoml.file.TomlFileReader
import fairu.utils.auth.Token
import fairu.utils.Config
import fairu.routes.files.files
import fairu.routes.image
import fairu.routes.login
import fairu.routes.users.users
import fairu.utils.Version
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.cio.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import naibu.ext.koin.get
import naibu.ext.ktor.server.plugins.logging.RequestLogging
import naibu.ext.ktor.server.setupServer
import naibu.logging.logging
import naibu.math.toIntSafe
import naibu.serialization.DefaultFormats
import naibu.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.noelware.remi.s3.S3StorageTrailer

val log by logging("fairu.application")
val configPath = System.getProperty("fairu.config-path") ?: "fairu.toml"

/**
 * Fairu application.
 *
 * ## Details
 * - Storage:  S3 via Remi
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
    log.info { "* Initializing storage trailer" }
    get<S3StorageTrailer>().init()

    /* setup server */
    val server = setupServer(CIO) {
        production = true

        host = config.server.host
        port = config.server.port.toIntSafe()

        server {
            install(RequestLogging) {
                loggerName = "fairu.server.requests"
            }

            install(ContentNegotiation) {
                json(DefaultFormats.Json)
            }

            authentication {
                jwt {
                    realm = "fairu"

                    verifier(Token.verifier)

                    validate { cred ->
                        if (cred.payload.subject.isNullOrBlank()) {
                            null
                        } else {
                            JWTPrincipal(cred.payload)
                        }
                    }

                    challenge { scheme, realm -> call.respond(HttpStatusCode.Unauthorized) }
                }
            }

            routing {
                // api
                route("/v1") {
                    // /login
                    login()

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
