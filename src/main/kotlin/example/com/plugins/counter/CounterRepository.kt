package example.com.plugins.counter

import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class CounterObject(val name: String, val currentVal: Int)

class CounterRepository(database: Database) {

    object Counters : Table("counters") {
        private val id = integer("id").autoIncrement()
        val name = varchar("name", length = 50).uniqueIndex()
        val currentVal = integer("current_val")

        override val primaryKey = PrimaryKey(id)
    }

    init {
        transaction(database) {
            SchemaUtils.create(Counters)
        }
    }

    suspend fun create(counterObject: CounterObject): Boolean {
        return dbQuery {
            try {
                Counters.insert {
                    it[name] = counterObject.name
                    it[currentVal] = 0
                }
                return@dbQuery true
            } catch (ex: ExposedSQLException) {
                return@dbQuery false
            }
        }
    }

    suspend fun read(name: String): CounterObject? {
        return dbQuery {
            Counters.selectAll()
                .where { Counters.name eq name }
                .map { CounterObject(it[Counters.name], it[Counters.currentVal]) }
                .singleOrNull()
        }
    }

    suspend fun readAll(): List<CounterObject> {
        return dbQuery {
            Counters.selectAll()
                .map { CounterObject(it[Counters.name], it[Counters.currentVal]) }
        }
    }

    suspend fun increment(name: String): CounterObject? {
        return dbQuery {
            //Update returning is not supported by H2, so selecting counter object after update
            val updatedCount = Counters.update(where = { Counters.name eq name }) {
                with(SqlExpressionBuilder) {
                    it.update(currentVal, currentVal + 1)
                }
            }
            if (updatedCount != 0) {
                Counters.selectAll()
                    .where { Counters.name eq name }
                    .map { CounterObject(it[Counters.name], it[Counters.currentVal]) }
                    .singleOrNull()
            } else {
                null
            }
        }
    }

    suspend fun delete(name: String) {
        dbQuery {
            Counters.deleteWhere { Counters.name.eq(name) }
        }
    }

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}

