package fairu

import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.runtime.endpoint.AwsEndpoint
import aws.sdk.kotlin.runtime.endpoint.AwsEndpointResolver
import aws.sdk.kotlin.runtime.endpoint.CredentialScope
import aws.sdk.kotlin.services.s3.S3Client
import aws.smithy.kotlin.runtime.auth.awscredentials.Credentials
import aws.smithy.kotlin.runtime.auth.awscredentials.CredentialsProvider
import aws.smithy.kotlin.runtime.http.engine.ktor.KtorEngine
import aws.smithy.kotlin.runtime.util.InternalApi
import fairu.utils.Config
import fairu.utils.mongo.Database
import fairu.utils.mongo.DatabaseClient
import io.ktor.client.engine.cio.*
import naibu.logging.logging
import org.apache.tika.Tika
import org.koin.dsl.bind
import org.koin.dsl.module
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

@OptIn(InternalApi::class)
val fairuModule = module {
    val log by logging("fairu.dependencies")

    single {
        Tika()
    }

    /* storage */
    single(createdAtStart = true) {
        val config = get<Config.Fairu>()
        S3Client {
            endpointResolver = AwsEndpointResolver { _, _ ->
                AwsEndpoint(config.s3.endpoint, null)
            }

            credentialsProvider = StaticCredentialsProvider(
                Credentials(config.s3.accessKey, config.s3.secretKey)
            )

            httpClientEngine = KtorEngine(CIO.create {})
            region = "us-west-2"
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
