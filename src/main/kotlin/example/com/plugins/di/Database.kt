package example.com.plugins.di

import example.com.plugins.counter.CounterRepository
import org.jetbrains.exposed.sql.Database
import org.koin.core.module.Module
import org.koin.dsl.module

fun configureDatabaseDIModule(): Module {

    return module {
        single<Database> {
            Database.connect(
                url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
                user = "root",
                driver = "org.h2.Driver",
                password = "",
            )
        }
    }

}
