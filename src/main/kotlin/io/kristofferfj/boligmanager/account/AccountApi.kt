package io.kristofferfj.boligmanager.account

import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route

object AccountApi {

    fun Route.account() {
        route("/account") {
            post {
                val accountInput = call.receive<NewAccountInput>()
                val createdAccount = createAccount(accountInput.name, accountInput.number, accountInput.companyId)
                call.respond(HttpStatusCode.OK, createdAccount)
            }

            get("all") {
                call.respond(HttpStatusCode.OK, getAccounts())
            }

            get("{id}") {
                val id = call.parameters["id"]!!.toLong()
                call.respond(HttpStatusCode.OK, getAccount(id))
            }

            delete("{id}") {
                val id = call.parameters["id"]!!.toLong()
                call.respond(HttpStatusCode.OK, deleteAccount(id))
            }
        }
    }

    data class NewAccountInput(
        val name: String,
        val number: Int,
        val companyId: Long,
    )
}
