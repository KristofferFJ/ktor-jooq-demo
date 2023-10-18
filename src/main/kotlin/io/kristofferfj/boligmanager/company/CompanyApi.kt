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
                val companyInput = call.receive<NewCompanyInput>()
                val createdCompany = createCompany(companyInput.name)
                call.respond(HttpStatusCode.OK, createdCompany)
            }

            get("all") {
                call.respond(HttpStatusCode.OK, getCompanies())
            }

            get("{id}") {
                val id = call.parameters["id"]!!.toLong()
                call.respond(HttpStatusCode.OK, getCompany(id))
            }

            delete("{id}") {
                val id = call.parameters["id"]!!.toLong()
                call.respond(HttpStatusCode.OK, deleteCompany(id))
            }
        }
    }

    data class NewCompanyInput(
        val name: String,
    )
}
