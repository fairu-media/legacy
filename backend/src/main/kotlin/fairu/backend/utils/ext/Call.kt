package fairu.backend.utils.ext

import fairu.backend.utils.BasicResponse
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import kotlinx.serialization.SerializationStrategy
import naibu.serialization.DefaultFormats
import naibu.serialization.json.Json

val PipelineContext<Unit, ApplicationCall>.log get() = call.application.environment.log

suspend inline fun <reified T> PipelineContext<Unit, ApplicationCall>.respond(data: T) {
    call.respond(BasicResponse(data, true))
}

suspend fun <T> PipelineContext<Unit, ApplicationCall>.respond(serializer: SerializationStrategy<T>, data: T) {
    val element = DefaultFormats.Json.encodeToJsonElement(serializer, data)
    call.respond(BasicResponse(element, true))
}
