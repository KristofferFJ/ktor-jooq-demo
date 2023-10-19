package io.kristofferfj;

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.kristofferfj.boligmanager.account.AccountApi.account
import io.kristofferfj.boligmanager.booking.BookingApi.booking
import io.kristofferfj.boligmanager.company.CompanyApi.company
import io.kristofferfj.boligmanager.estate.EstateApi.estate
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.response.respondText
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main() {
    //migrateDatabase()
    embeddedServer(Netty, port = 314, module = Application::module).start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        jackson {
            registerModule(JavaTimeModule())
            disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        }
    }

    install(StatusPages) {
        exception<Throwable> { cause ->
            call.respondText(cause.localizedMessage, status = HttpStatusCode.InternalServerError)
        }
    }

    routing {
        account()
        booking()
        company()
        estate()
    }
}
