package io.kristofferfj.boligmanager.booking

import io.kristofferfj.boligmanager.account.getAccounts
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.route

object BookingApi {

    fun Route.booking() {
        route("/booking") {
            get("all") {
                call.respond(HttpStatusCode.OK, getAccounts())
            }
        }
    }
}
