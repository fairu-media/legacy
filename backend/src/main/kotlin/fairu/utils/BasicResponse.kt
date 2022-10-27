package fairu.utils

import kotlinx.serialization.Serializable

@Serializable
data class BasicResponse<T>(val data: T, val success: Boolean)
