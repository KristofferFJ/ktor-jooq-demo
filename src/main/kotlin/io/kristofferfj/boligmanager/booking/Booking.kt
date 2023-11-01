package io.kristofferfj.boligmanager.booking

import io.kristofferfj.Database
import io.kristofferfj.jooq.public_.tables.Booking.BOOKING
import io.kristofferfj.jooq.public_.tables.records.BookingRecord
import java.math.BigDecimal
import org.springframework.transaction.annotation.Transactional

data class Booking(
    val id: Long,
    val amount: BigDecimal,
    val bookingSetId: Long,
    val accountId: Long,
)

fun getBookings(): List<Booking> {
    return Database.getDslContext().selectFrom(BOOKING).map { it.toBooking() }
}

fun createNewBooking(input: NewBookingInput): List<Booking> {
    val bookingSetId = createBookingSet(
        companyId = input.companyId,
        estateId = input.estateId,
    ).id
    check(input.amount.times(BigDecimal("100")).remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) == 0) {
        "Only a precision of 2 is supported for amount"
    }
    return Database.getDslContext().insertInto(BOOKING, BOOKING.BOOKING_SET_ID, BOOKING.AMOUNT, BOOKING.ACCOUNT_ID)
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












