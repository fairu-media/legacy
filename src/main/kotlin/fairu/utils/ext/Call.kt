package fairu.utils.ext

import fairu.utils.BasicResponse
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import kotlinx.serialization.SerializationStrategy
import naibu.serialization.DefaultFormats
import naibu.serialization.json.Json

typealias CallContext = PipelineContext<Unit, ApplicationCall>

val CallContext.log get() = call.application.environment.log

suspend inline fun <reified T> CallContext.respond(data: T) {
    call.respond(BasicResponse(data, true))
}

suspend fun <T> CallContext.respond(serializer: SerializationStrategy<T>, data: T) {
    val element = DefaultFormats.Json.encodeToJsonElement(serializer, data)
    call.respond(BasicResponse(element, true))
}
