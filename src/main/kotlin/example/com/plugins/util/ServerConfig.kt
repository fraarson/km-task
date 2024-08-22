package example.com.plugins.util

import example.com.module
import io.ktor.network.tls.certificates.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.httpsredirect.*
import java.io.File
import java.security.KeyStore

fun configureNettyEnvironment(keyStore: KeyStore, keyStoreFile: File): ApplicationEngineEnvironment {
    return applicationEngineEnvironment {
        connector {
            port = 8080
        }
        sslConnector(
            keyStore = keyStore,
            keyAlias = "localhost",
            keyStorePassword = { "password".toCharArray() },
            privateKeyPassword = { "password".toCharArray() }) {
            port = 8443
            keyStorePath = keyStoreFile
        }
        module(Application::module)
    }
}

fun configureLocalhostKeystore(): Pair<KeyStore, File> {
    val keyStoreFile = File("build/keystore.jks")
    val keyStore = buildKeyStore {
        certificate("localhost") {
            password = "password"
            domains = listOf("127.0.0.1", "0.0.0.0", "localhost")
        }
    }
    keyStore.saveToFile(keyStoreFile, "password")
    return Pair(keyStore, keyStoreFile)
}

fun Application.configureHttpsAndCors() {
    install(HttpsRedirect) {
        sslPort = 8443
        permanentRedirect = true
    }

    install(CORS) {
        anyHost()
    }
}