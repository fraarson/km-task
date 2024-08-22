package example.com.plugins.di

import example.com.plugins.counter.CounterRepository
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin

fun Application.configureDependencyInjection() {

    val counterRepositoryModule = module {
        single<CounterRepository> { CounterRepository(inject<Database>().value) }
    }

    install(Koin) {
        modules(configureDatabaseDIModule(), counterRepositoryModule)
    }
}