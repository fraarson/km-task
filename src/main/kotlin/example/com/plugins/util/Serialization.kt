package example.com.plugins.util

import io.ktor.serialization.jackson.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*

fun Application.configureSerialization() {

    install(ContentNegotiation) {
        jackson()
    }

}
