package io.kristofferfj.boligmanager.company

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route

object CompanyApi {

    fun Route.company() {
        route("/company") {
            post {
                val companyInput = call.receive<String>()
                val createdCompany = createCompany(companyInput)
                call.respond(HttpStatusCode.OK, createdCompany)
            }

            get("all") {
                call.respond(HttpStatusCode.OK, getCompanies())
            }

            delete("{id}") {
                val id = call.parameters["id"]!!.toInt()
                call.respond(HttpStatusCode.OK, deleteCompany(id))
            }
        }
    }
}
