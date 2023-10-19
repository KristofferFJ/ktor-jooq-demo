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

fun createNewBooking(input: NewBookingInput): List<Booking> {
    val bookingSetId = createBookingSet(companyId = input.companyId, estateId = input.estateId).id
    return Database.connect().insertInto(BOOKING, BOOKING.BOOKING_SET_ID, BOOKING.AMOUNT, BOOKING.ACCOUNT_ID)
        .values(bookingSetId, input.amount, input.debitAccountId)
        .values(bookingSetId, input.amount.negate(), input.creditAccountId)
        .returning()
        .map { it.toBooking() }
}

fun BookingRecord.toBooking(): Booking {
    return Booking(
        this[BOOKING.ID],
        this[BOOKING.AMOUNT],
        this[BOOKING.BOOKING_SET_ID],
        this[BOOKING.ACCOUNT_ID],
    )
}

data class NewBookingInput(
    val amount: BigDecimal,
    val debitAccountId: Long,
    val creditAccountId: Long,
    val companyId: Long?,
    val estateId: Long?,
)
