package example.com.plugins.util

import com.papsign.ktor.openapigen.OpenAPIGen
import com.papsign.ktor.openapigen.openAPIGen
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureSwagger() {

    install(OpenAPIGen) {
        serveOpenApiJson = true
        serveSwaggerUi = true
        info {
            title = "Counter API"

        }
        server("https://localhost:8443/") {
            description = "Local deployment"
        }
    }

    routing {
        get("/openapi.json") {
            call.respond(application.openAPIGen.api.serialize())
        }
        get("/") {
            call.respondRedirect("/swagger-ui/index.html?url=/openapi.json", true)
        }
    }

}

