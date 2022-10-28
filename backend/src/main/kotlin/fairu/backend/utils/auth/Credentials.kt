package fairu.backend.utils.auth

import kotlinx.serialization.Serializable

@Serializable
data class Credentials(val username: String, val password: String)
