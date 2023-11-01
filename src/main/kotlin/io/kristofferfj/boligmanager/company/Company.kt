package io.kristofferfj.boligmanager.company

import io.kristofferfj.Database
import io.kristofferfj.jooq.public_.tables.Company.COMPANY
import io.kristofferfj.jooq.public_.tables.Estate
import io.kristofferfj.jooq.public_.tables.records.CompanyRecord
import java.time.LocalDate

data class Company(
    val id: Long,
    val name: String,
    val createdAt: LocalDate,
)

fun createCompany(name: String): Company {
    val dsl = Database.getDslContext()
    check(!dsl.fetchExists(dsl.selectFrom(COMPANY).where(COMPANY.NAME.eq(name)))) { " Name already exists" }
    return dsl.insertInto(COMPANY, COMPANY.NAME)
        .values(name)
        .returning()
        .single()
        .toCompany()
}

fun getCompanies(): List<Company> {
    return Database.getDslContext().selectFrom(COMPANY).map { it.toCompany() }
}

fun getCompany(id: Long): Company {
    return Database.getDslContext().selectFrom(COMPANY).where(COMPANY.ID.eq(id)).single().toCompany()
}

fun deleteCompany(estateId: Long): Boolean {
    return Database.getDslContext().deleteFrom(Estate.ESTATE)
        .where(Estate.ESTATE.ID.eq(estateId))
        .execute() > 0
}

private fun CompanyRecord.toCompany(): Company {
    return Company(id = this[COMPANY.ID], name = this[COMPANY.NAME], createdAt = LocalDate.now())
}
