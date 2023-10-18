package io.kristofferfj.boligmanager

import io.kristofferfj.Database
import  io.kristofferfj.jooq.public_.tables.Company.COMPANY

data class Company(
    val id: Int,
    val name: String,
)

fun createCompany(name: String): Company {
    val dsl = Database.connect()
    val companyId = dsl.insertInto(COMPANY, COMPANY.NAME)
        .values(name)
        .returning(COMPANY.ID)
        .fetchOne()
        ?.getValue(COMPANY.ID)

    // Check if ID is not null or handle error
    return Company(id = companyId!!, name = name)
}
