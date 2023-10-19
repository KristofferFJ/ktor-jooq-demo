package io.kristofferfj.boligmanager.booking

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route

object BookingApi {

    fun Route.booking() {
        route("/booking") {
            get("all") {
                call.respond(HttpStatusCode.OK, getBookings())
            }

            post {
                val newBookingInput = call.receive<NewBookingInput>()
                call.respond(HttpStatusCode.OK, createNewBooking(newBookingInput))
            }

            get("/set/{id}") {
                call.respond(HttpStatusCode.OK, getBookingSetsAndBookings(call.parameters["id"]!!.toLong()))
            }
        }
    }
}
