package io.kristofferfj.boligmanager.booking_set

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.route

object BookingSetApi {

    fun Route.bookingSet() {
        route("/booking-set") {
            get("all") {
                call.respond(HttpStatusCode.OK, getBookingSets())
            }
        }
    }
}
