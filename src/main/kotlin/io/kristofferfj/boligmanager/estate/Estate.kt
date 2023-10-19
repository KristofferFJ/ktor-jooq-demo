package io.kristofferfj.boligmanager.estate

import io.kristofferfj.Database
import io.kristofferfj.jooq.public_.tables.Estate.ESTATE
import io.kristofferfj.jooq.public_.tables.records.EstateRecord

data class Estate(
    val id: Long,
    val name: String,
    val companyId: Long,
)

fun createEstate(name: String, companyId: Long): Estate {
    return Database.connect().insertInto(ESTATE, ESTATE.NAME, ESTATE.COMPANY_ID)
        .values(name, companyId)
        .returning()
        .single()
        .toEstate()
}

fun getEstate(id: Long): Estate {
    return Database.connect().selectFrom(ESTATE).where(ESTATE.ID.eq(id)).single().toEstate()
}

fun getEstates(): List<Estate> {
    return Database.connect().selectFrom(ESTATE).map { it.toEstate() }
}

fun deleteEstate(estateId: Long): Boolean {
    return Database.connect().deleteFrom(ESTATE)
        .where(ESTATE.ID.eq(estateId))
        .execute() > 0
}

private fun EstateRecord.toEstate(): Estate {
    return Estate(id = this[ESTATE.ID], name = this[ESTATE.NAME], companyId = this[ESTATE.COMPANY_ID])
}
