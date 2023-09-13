package fairu.backend.utils

import kotlinx.serialization.Serializable

@Serializable
data class BasicResponse<T>(val data: T, val success: Boolean)

@Serializable
data class Message(val message: String)
