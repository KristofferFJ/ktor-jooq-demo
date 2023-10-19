package io.kristofferfj.boligmanager.booking

import io.kristofferfj.Database
import io.kristofferfj.jooq.public_.tables.Booking.BOOKING
import io.kristofferfj.jooq.public_.tables.BookingSet.BOOKING_SET
import io.kristofferfj.jooq.public_.tables.records.BookingSetRecord
import java.time.LocalDateTime
import org.jooq.Field
import org.jooq.Records.mapping
import org.jooq.impl.DSL.field
import org.jooq.impl.DSL.multiset
import org.jooq.impl.DSL.selectFrom

data class BookingSet(
    val id: Long,
    val createdAt: LocalDateTime,
    val estateId: Long?,
    val companyId: Long?,
)

data class BookingSetAndBooking(
    val id: Long,
    val createdAt: LocalDateTime,
    val estateId: Long?,
    val companyId: Long?,
    val bookings: List<Booking>,
)

fun createBookingSet(estateId: Long?, companyId: Long?): BookingSet {
    check((estateId == null) != (companyId == null)) { "Exactly 1 of estateId or companyId must be not-null" }
    return Database.connect().insertInto(
        BOOKING_SET,
        BOOKING_SET.COMPANY_ID,
        BOOKING_SET.ESTATE_ID,
        BOOKING_SET.CREATED_AT,
    ).values(companyId, estateId, LocalDateTime.now())
        .returning(BOOKING_SET.ID)
        .single()
        .toBookingSet()
}

fun getBookingSets(): List<BookingSet> {
    return Database.connect().selectFrom(BOOKING_SET).map { it.toBookingSet() }
}

fun getBookingSetsAndBookings(id: Long): BookingSetAndBooking {
    return Database.connect().select(
        BOOKING_SET.ID,
        BOOKING_SET.CREATED_AT,
        BOOKING_SET.ESTATE_ID,
        BOOKING_SET.COMPANY_ID,
        multiset(
            selectFrom(BOOKING)
                .where(BOOKING.BOOKING_SET_ID.eq(BOOKING_SET.ID))
        ).`as`("bookings").convertFrom { bookings -> bookings.map { it.toBooking() } }
    ).from(BOOKING_SET)
        .where(BOOKING_SET.ID.eq(id))
        .fetchSingleInto(BookingSetAndBooking::class.java)
}

private fun BookingSetRecord.toBookingSet(): BookingSet {
    return BookingSet(
        this[BOOKING_SET.ID],
        this[BOOKING_SET.CREATED_AT],
        this[BOOKING_SET.ESTATE_ID],
        this[BOOKING_SET.COMPANY_ID],
    )
}