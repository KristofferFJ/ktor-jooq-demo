package io.kristofferfj.boligmanager.booking

import io.kristofferfj.Database
import io.kristofferfj.jooq.public_.tables.Booking.BOOKING
import io.kristofferfj.jooq.public_.tables.records.BookingRecord
import java.math.BigDecimal

data class Booking(
    val id: Long,
    val amount: BigDecimal,
    val bookingSetId: Long,
    val accountId: Long,
)

fun getBookings(): List<Booking> {
    return Database.connect().selectFrom(BOOKING).map { it.toBooking() }
}

private fun BookingRecord.toBooking(): Booking {
    return Booking(
        this[BOOKING.ID],
        this[BOOKING.AMOUNT],
        this[BOOKING.BOOKING_SET_ID],
        this[BOOKING.ACCOUNT_ID],
    )
}