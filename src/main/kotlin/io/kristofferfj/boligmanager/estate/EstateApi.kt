package io.kristofferfj.boligmanager.estate

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import java.time.LocalDate

object EstateApi {

    fun Route.estate() {
        route("/estate") {
            post {
                val estateInput = call.receive<NewEstateInput>()
                val createdEstate = createEstate(
                    name = estateInput.name,
                    companyId = estateInput.companyId,
                    someDate = estateInput.someDate,
                )
                call.respond(HttpStatusCode.OK, createdEstate)
            }

            get("all") {
                call.respond(HttpStatusCode.OK, getEstates())
            }

            get("{id}") {
                val id = call.parameters["id"]!!.toLong()
                call.respond(HttpStatusCode.OK, getEstate(id))
            }

            delete("{id}") {
                val id = call.parameters["id"]!!.toLong()
                call.respond(HttpStatusCode.OK, deleteEstate(id))
            }
        }
    }

    data class NewEstateInput(
        val name: String,
        val companyId: Long,
        val someDate: LocalDate,
    )
}
