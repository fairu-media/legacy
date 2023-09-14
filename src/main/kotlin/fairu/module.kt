package fairu

import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.s3.S3Client
import aws.smithy.kotlin.runtime.auth.awscredentials.Credentials
import aws.smithy.kotlin.runtime.net.Url
import fairu.utils.Config
import fairu.utils.mongo.Database
import fairu.utils.mongo.DatabaseClient
import naibu.ext.ktor.client.HttpClientTools
import naibu.logging.logging
import org.koin.dsl.bind
import org.koin.dsl.module
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import org.overviewproject.mime_types.MimeTypeDetector

val fairuModule = module {
    val log by logging("fairu.backend.dependencies")

    single { HttpClientTools.createDefaultHttpClient() }

    single { MimeTypeDetector() }

    /* storage */
    single(createdAtStart = true) {
        val config = get<Config.Fairu>()
        S3Client {
            region = config.s3.region.value
            endpointUrl = Url.parse(config.s3.endpoint)
            forcePathStyle = config.s3.pathStyleAccessEnabled
            credentialsProvider = StaticCredentialsProvider(Credentials(config.s3.accessKey, config.s3.secretKey))
        }
    }

    /* mongodb */
    single(createdAtStart = true) {
        val config = get<Config.Fairu>()
        log.info { "* Connecting to '${config.db.uri}'" }
        KMongo.createClient(config.db.uri).coroutine
    } bind DatabaseClient::class

    single(createdAtStart = true) {
        val config = get<Config.Fairu>()
        get<CoroutineClient>().getDatabase(config.db.databaseName)
    } bind Database::class
}
