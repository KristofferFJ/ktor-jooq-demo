package io.kristofferfj.boligmanager.company

import io.kristofferfj.Database
import io.kristofferfj.jooq.public_.tables.Company.COMPANY
import io.kristofferfj.jooq.public_.tables.Estate
import io.kristofferfj.jooq.public_.tables.records.CompanyRecord

data class Company(
    val id: Long,
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

fun getCompanies(): List<Company> {
    return Database.connect().selectFrom(COMPANY).map { it.toCompany() }
}

fun getCompany(id: Long): Company {
    return Database.connect().selectFrom(COMPANY).where(COMPANY.ID.eq(id)).single().toCompany()
}

fun deleteCompany(estateId: Long): Boolean {
    return Database.connect().deleteFrom(Estate.ESTATE)
        .where(Estate.ESTATE.ID.eq(estateId))
        .execute() > 0
}

private fun CompanyRecord.toCompany(): Company {
    return Company(id = this[COMPANY.ID], name = this[COMPANY.NAME])
}
