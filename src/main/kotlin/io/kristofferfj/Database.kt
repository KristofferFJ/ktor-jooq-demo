package io.kristofferfj

import java.sql.Connection
import java.sql.DriverManager
import org.flywaydb.core.Flyway
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL

object Database {

    private const val URL = "jdbc:postgresql://localhost:5432/ktor_jooq"
    private const val USER = "myuser"
    private const val PASSWORD = "mypass"
    private val dslContext = connect()

    fun getDslContext(): DSLContext {
        return this.dslContext
    }

    fun connect(): DSLContext {
        val connection: Connection = DriverManager.getConnection(URL, USER, PASSWORD)
        return DSL.using(connection, SQLDialect.POSTGRES)
    }

    fun cleanMigrate() {
        val flyway = Flyway.configure()
            .dataSource(URL, USER, PASSWORD)
            .cleanDisabled(false)
            .load()
        flyway.clean()
        flyway.migrate()
    }
}
