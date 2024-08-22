package example.com

import example.com.plugins.counter.CounterObject
import example.com.plugins.counter.configureCounterRouting
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

class CountersRoutingTest {

    @Test
    fun testCreateCounter() = testApplication {

        val client = configureAndGetClient()

        client.post("/counters", {
            url {
                protocol = URLProtocol.HTTPS
            }
            contentType(ContentType.Application.Json)
            setBody(CounterObject("name", 0))
        }).apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Ok", bodyAsText())
        }
    }

    @Test
    fun testGetAllCounters() = testApplication {

        val client = configureAndGetClient()

        client.get("/counters", {
            url {
                protocol = URLProtocol.HTTPS
            }
            contentType(ContentType.Application.Json)
            setBody(CounterObject("name", 0))
        }).apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("[]", bodyAsText())
        }
    }

    @Test
    fun testGetNonExistingCounter() = testApplication {

        val client = configureAndGetClient()

        client.get("/counters/abcd", {
            url {
                protocol = URLProtocol.HTTPS
            }
            contentType(ContentType.Application.Json)
            setBody(CounterObject("name", 0))
        }).apply {
            assertEquals(HttpStatusCode.NotFound, status)
            assertEquals("", bodyAsText())
        }
    }

    @Test
    fun testIncrementNonExistingCounter() = testApplication {

        val client = configureAndGetClient()

        client.post("/counters/abcd/increment", {
            url {
                protocol = URLProtocol.HTTPS
            }
        }).apply {
            assertEquals(HttpStatusCode.NotFound, status)
            assertEquals("", bodyAsText())
        }
    }

    @Test
    fun testDeleteNonExistingCounter() = testApplication {

        val client = configureAndGetClient()

        client.delete("/counters/abcd", {
            url {
                protocol = URLProtocol.HTTPS
            }
            contentType(ContentType.Application.Json)
            setBody(CounterObject("name", 0))
        }).apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Deleted", bodyAsText())
        }
    }

    private fun ApplicationTestBuilder.configureAndGetClient(): HttpClient {
        application {
            configureCounterRouting()
        }

        return createClient {
            install(ContentNegotiation) {
                jackson()
            }
        }
    }
}