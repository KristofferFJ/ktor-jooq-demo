package io.kristofferfj.boligmanager.booking_set

import io.kristofferfj.Database
import io.kristofferfj.jooq.public_.tables.BookingSet.BOOKING_SET
import io.kristofferfj.jooq.public_.tables.records.BookingSetRecord
import java.time.LocalDateTime

data class BookingSet(
    val id: Long,
    val createdAt: LocalDateTime,
    val estateId: Long?,
    val companyId: Long?,
)

fun createBookingSet(estateId: Long?, companyId: Long?): BookingSet {
    check((estateId == null) != (companyId == null))
    val createdAt = LocalDateTime.now()

    val dsl = Database.connect()
    val bookingSetId = dsl.insertInto(
        BOOKING_SET,
        BOOKING_SET.COMPANY_ID,
        BOOKING_SET.ESTATE_ID,
        BOOKING_SET.CREATED_AT,
    ).values(companyId, estateId, createdAt)
        .returning(BOOKING_SET.ID)
        .fetchOne()
        ?.getValue(BOOKING_SET.ID)

    return BookingSet(id = bookingSetId!!, companyId = companyId, estateId = estateId, createdAt = createdAt)
}

fun getBookingSets(): List<BookingSet> {
    return Database.connect().selectFrom(BOOKING_SET).map { it.toBookingSet() }
}

private fun BookingSetRecord.toBookingSet(): BookingSet {
    return BookingSet(
        this[BOOKING_SET.ID],
        this[BOOKING_SET.CREATED_AT],
        this[BOOKING_SET.ESTATE_ID],
        this[BOOKING_SET.COMPANY_ID],
    )
}