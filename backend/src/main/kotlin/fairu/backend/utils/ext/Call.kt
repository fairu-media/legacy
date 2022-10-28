package fairu.backend.utils.ext

import fairu.backend.utils.BasicResponse
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*

val PipelineContext<Unit, ApplicationCall>.log get() = call.application.environment.log

suspend inline fun <reified T> PipelineContext<Unit, ApplicationCall>.respond(data: T) {
    call.respond(BasicResponse(data, true))
}
