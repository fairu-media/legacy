package fairu.backend.utils

import aws.sdk.kotlin.services.s3.model.BucketLocationConstraint
import aws.smithy.kotlin.runtime.auth.awscredentials.Credentials
import fairu.backend.utils.s3.Region
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Config(val fairu: Fairu) {
    @Serializable
    data class Fairu(
        val management: Management = Management(),
        val frontend: Frontend = Frontend(),
        val server: Server = Server(),
        val s3: S3,
        val jwt: Jwt,
        val db: Database,
    ) {
        @Serializable
        data class Management(
            /** Usernames allowed to be used in POST /v1/users */
            @SerialName("allowed-usernames")
            val allowedUsernames: List<String>? = null,
        )

        @Serializable
        data class Frontend(
            /** Whether frontend-specific routes should be added. */
            val enabled: Boolean = false,
            val url: String? = null
        )

        @Serializable
        data class Server(
            val host: String = "0.0.0.0",
            val port: Long = 3232 // we use a long because Ktoml is shit
        )

        @Serializable
        data class Database(
            val uri: String,
            @SerialName("database-name")
            val databaseName: String
        )

        @Serializable
        data class S3(
            val region: Region,
            val bucket: String,

            // endpoint config
            @SerialName("path-style-access-enabled")
            val pathStyleAccessEnabled: Boolean = false,
            val endpoint: String,

            // credentials
            @SerialName("access-key")
            val accessKey: String,
            @SerialName("secret-key")
            val secretKey: String,
        ) {
        }

        @Serializable
        data class Jwt(
            val secret: String
        )
    }
}
