package io.kristofferfj

import java.sql.Connection
import java.sql.DriverManager
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL

object Database {

    // Update these parameters according to your database setup
    private const val url = "jdbc:postgresql://localhost:5432/ktor_jooq"
    private const val user = "myuser"
    private const val password = "mypass"

    fun connect(): DSLContext {
        val connection: Connection = DriverManager.getConnection(url, user, password)
        return DSL.using(connection, SQLDialect.POSTGRES)
    }
}
