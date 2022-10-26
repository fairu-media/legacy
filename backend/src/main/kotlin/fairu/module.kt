package fairu

import fairu.utils.Config
import fairu.mongo.Database
import fairu.mongo.DatabaseClient
import naibu.logging.logging
import org.koin.dsl.bind
import org.koin.dsl.module
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import org.noelware.remi.s3.S3Provider
import org.noelware.remi.s3.S3StorageTrailer

val fairuModule = module {
    val log by logging("fairu.dependencies")

    /* storage */
    single(createdAtStart = true) {
        val config = get<Config.Fairu>()
        S3StorageTrailer {
            provider  = S3Provider.Custom
            bucket    = config.s3.accessKey
            accessKey = config.s3.accessKey
            secretKey = config.s3.secretKey
            endpoint  = config.s3.endpoint
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
