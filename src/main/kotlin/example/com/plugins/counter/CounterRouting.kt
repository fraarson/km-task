package example.com.plugins.counter

import com.papsign.ktor.openapigen.annotations.parameters.PathParam
import com.papsign.ktor.openapigen.route.apiRouting
import com.papsign.ktor.openapigen.route.path.normal.delete
import com.papsign.ktor.openapigen.route.path.normal.get
import com.papsign.ktor.openapigen.route.path.normal.post
import com.papsign.ktor.openapigen.route.response.respond
import com.papsign.ktor.openapigen.route.route
import com.papsign.ktor.openapigen.route.throws
import io.ktor.http.*
import io.ktor.server.application.*
import org.koin.ktor.ext.inject

data class CounterNamePathParam(@PathParam("Counter Name Path Param") val name: String)

fun Application.configureCounterRouting() {

    // Ktor Idea plugin is available only in Ultimate, so instead of using it to generate openapi.yaml spec
    // I've used openapi lib (dev.forst:ktor-openapi-generator) which wraps ktor routes and generates openapi spec at the same time
    apiRouting {

        val counterRepository by inject<CounterRepository>()

        route("/counters") {
            post<Unit, String, CounterObject> { _, counter ->
                val isCreated = counterRepository.create(counter)
                if (isCreated) {
                    respond("Ok")
                } else {
                    throws(status = HttpStatusCode.UnprocessableEntity, exClass = IllegalArgumentException::class)
                }
            }
            get<Unit, List<CounterObject>> {
                val allCounters = counterRepository.readAll()
                respond(allCounters)
            }
        }

        route("/counters/{name}") {
            get<CounterNamePathParam, CounterObject> { pathParameter ->
                val counter = counterRepository.read(pathParameter.name)
                if (counter != null) {
                    respond(counter)
                } else {
                    throws(HttpStatusCode.NotFound, exClass = IllegalArgumentException::class)
                }
            }
            delete<CounterNamePathParam, String> { pathParameter ->
                counterRepository.delete(pathParameter.name)
                respond("Deleted")
            }
        }

        route("/counters/{name}/increment") {
            post<CounterNamePathParam, CounterObject, String> { pathParameter, _ ->
                val incrementedCounter = counterRepository.increment(pathParameter.name)
                if (incrementedCounter != null) {
                    respond(incrementedCounter)
                } else {
                    throws(HttpStatusCode.NotFound, exClass = IllegalArgumentException::class)
                }
            }
        }

    }

}