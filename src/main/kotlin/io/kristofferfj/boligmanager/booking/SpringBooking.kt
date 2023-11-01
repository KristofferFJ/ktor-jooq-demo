package io.kristofferfj.boligmanager.booking

import io.kristofferfj.Database
import io.kristofferfj.jooq.public_.tables.Booking.BOOKING
import java.math.BigDecimal
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

open class SpringBooking {

    @Transactional(propagation = Propagation.MANDATORY)
    open fun createNewBookingInTransactionSpring(input: NewBookingInput): List<Booking> {
        val dsl = Database.connect()
        val bookingSetId = createBookingSet(companyId = input.companyId, estateId = input.estateId, dslContext = dsl).id
        check(input.amount.times(BigDecimal("100")).remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) == 0) {
            "Only a precision of 2 is supported for amount"
        }
        return dsl.insertInto(
            BOOKING, BOOKING.BOOKING_SET_ID, BOOKING.AMOUNT, BOOKING.ACCOUNT_ID
        ).values(bookingSetId, input.amount, input.debitAccountId)
            .values(bookingSetId, input.amount.negate(), input.creditAccountId)
            .returning()
            .map { it.toBooking() }
    }
}