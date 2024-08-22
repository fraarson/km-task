package example.com

import example.com.plugins.counter.configureCounterRouting
import example.com.plugins.di.configureDependencyInjection
import example.com.plugins.util.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main(args: Array<String>) {
    val (keyStore, keyStoreFile) = configureLocalhostKeystore()
    val environment = configureNettyEnvironment(keyStore, keyStoreFile)
    embeddedServer(Netty, environment).start(wait = true)
}

fun Application.module() {
    configureHttpsAndCors()
    configureDependencyInjection()
    configureSerialization()
    configureSwagger()
    configureCounterRouting()
}
