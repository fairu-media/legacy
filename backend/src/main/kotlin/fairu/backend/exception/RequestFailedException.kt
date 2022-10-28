package fairu.backend.exception

import io.ktor.http.*

class RequestFailedException(val statusCode: HttpStatusCode, message: String) : RuntimeException(message)

fun failure(statusCode: HttpStatusCode, message: String): Nothing = throw RequestFailedException(statusCode, message)
