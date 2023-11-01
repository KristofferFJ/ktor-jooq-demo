package io.kristofferfj

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kristofferfj.Database.cleanMigrate
import io.kristofferfj.boligmanager.account.Account
import io.kristofferfj.boligmanager.account.AccountApi
import io.kristofferfj.boligmanager.account.createAccount
import io.kristofferfj.boligmanager.booking.Booking
import io.kristofferfj.boligmanager.booking.BookingSet
import io.kristofferfj.boligmanager.booking.NewBookingInput
import io.kristofferfj.boligmanager.booking.SpringBooking
import io.kristofferfj.boligmanager.booking.createBookingSet
import io.kristofferfj.boligmanager.booking.createNewBooking
import io.kristofferfj.boligmanager.booking.getBookingSets
import io.kristofferfj.boligmanager.booking.getBookings
import io.kristofferfj.boligmanager.booking.toBooking
import io.kristofferfj.boligmanager.company.Company
import io.kristofferfj.boligmanager.company.CompanyApi
import io.kristofferfj.boligmanager.company.createCompany
import io.kristofferfj.boligmanager.company.getCompany
import io.kristofferfj.boligmanager.estate.Estate
import io.kristofferfj.boligmanager.estate.EstateApi
import io.kristofferfj.jooq.public_.tables.Booking.BOOKING
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import org.jooq.impl.DSL
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


class Tests {

    private val objectMapper = jacksonObjectMapper().registerModule(JavaTimeModule())

    @Test
    fun testTransactionManagement() {
        val company = createCompany("testTransaction")
        val accountFrom = createAccount("from", 1234, companyId = company.id)
        val accountTo = createAccount("to", 4321, companyId = company.id)

        val firstBooking = createNewBooking(
            NewBookingInput(
                BigDecimal("1234.01"),
                accountFrom.id,
                accountTo.id,
                companyId = company.id,
            )
        )

        assertEquals(firstBooking.size, 2)

        val sets = getBookingSets()
        val bookings = getBookings()

        val exception = assertThrows<IllegalStateException> {
            createNewBooking(
                NewBookingInput(
                    BigDecimal("1234.012"),
                    accountFrom.id,
                    accountTo.id,
                    companyId = company.id,
                )
            )
        }

        assertEquals(exception.localizedMessage, "Only a precision of 2 is supported for amount")

        assertEquals(getBookingSets().size, sets.size.plus(1))
        assertEquals(getBookings().size, bookings.size)
    }

    @Test
    fun testTransactionManagementTransactionSpecified() {
        val company = createCompany("testTransaction")
        val accountFrom = createAccount("from", 1234, companyId = company.id)
        val accountTo = createAccount("to", 4321, companyId = company.id)

        val exception = assertThrows<IllegalStateException> {
            createNewBookingInTransaction( // new
                NewBookingInput(
                    BigDecimal("1234.012"),
                    accountFrom.id,
                    accountTo.id,
                    companyId = company.id,
                )
            )
        }

        assertEquals(exception.localizedMessage, "Only a precision of 2 is supported for amount")

        assertEquals(getBookingSets().size, 0)
        assertEquals(getBookings().size, 0)
    }

    @Test
    fun testTransactionManagementSpring() {
        val company = createCompany("testTransaction")
        val accountFrom = createAccount("from", 1234, companyId = company.id)
        val accountTo = createAccount("to", 4321, companyId = company.id)

        val exception = assertThrows<IllegalStateException> {
            SpringBooking().createNewBookingInTransactionSpring(
                NewBookingInput(
                    BigDecimal("1234.012"),
                    accountFrom.id,
                    accountTo.id,
                    companyId = company.id,
                )
            )
        }

        assertEquals(exception.localizedMessage, "Only a precision of 2 is supported for amount")

        // bookingSet not created
        assertEquals(getBookingSets().size, 0)
        assertEquals(getBookings().size, 0)
    }

    @Test
    fun `test company, estate, and booking endpoints`() {
        withTestApplication({ module() }) {

            // Test company creation
            val createdCompany: Company = handleRequest(HttpMethod.Post, "/company") {
                val payload = objectMapper.writeValueAsString(CompanyApi.NewCompanyInput("TestCompany"))
                setBody(payload)
                addHeader(HttpHeaders.ContentType, "application/json")
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
            }.let {
                objectMapper.readValue(it.response.content, Company::class.java)
            }

            // Test estate creation using the previously created company ID
            val createdEstate = handleRequest(HttpMethod.Post, "/estate") {
                val payload = objectMapper.writeValueAsString(EstateApi.NewEstateInput("TestEstate", createdCompany.id, LocalDate.now()))
                setBody(payload)
                addHeader(HttpHeaders.ContentType, "application/json")
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
            }.let {
                objectMapper.readValue(it.response.content, Estate::class.java)
            }

            // Create accounts
            val bankAccount = handleRequest(HttpMethod.Post, "/account") {
                val payload = objectMapper.writeValueAsString(
                    AccountApi.NewAccountInput(
                        name = "Bank",
                        number = 1002,
                        companyId = createdCompany.id,
                    )
                )
                setBody(payload)
                addHeader(HttpHeaders.ContentType, "application/json")
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
            }.let {
                objectMapper.readValue(it.response.content, Account::class.java)
            }

            val tenantAccount = handleRequest(HttpMethod.Post, "/account") {
                val payload = objectMapper.writeValueAsString(
                    AccountApi.NewAccountInput(
                        name = "Tenant",
                        number = 1001,
                        companyId = createdCompany.id,
                    )
                )
                setBody(payload)
                addHeader(HttpHeaders.ContentType, "application/json")
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
            }.let {
                objectMapper.readValue(it.response.content, Account::class.java)
            }

            // Test booking creation using the previously created company and estate IDs
            handleRequest(HttpMethod.Post, "/booking") {
                val payload = objectMapper.writeValueAsString(
                    NewBookingInput(
                        amount = BigDecimal("100.00"),
                        debitAccountId = tenantAccount.id,
                        creditAccountId = bankAccount.id,
                        companyId = null,
                        estateId = createdEstate.id
                    )
                )
                setBody(payload)
                addHeader(HttpHeaders.ContentType, "application/json")
            }.apply {
                assertEquals(HttpStatusCode.OK, response.status())
            }
        }
    }

    fun kotlinStuff() {
        //Immutable :strong:
        val companyNames = listOf("Rip", "Rap", "Rup")
        //companyNames.add("abs")

        val companies: List<Company> = companyNames.mapIndexed { index, name ->
            Company(
                id = index.toLong(),
                name = name,
                createdAt = LocalDate.now(),
            )
        }

        //Named parameters, see above and
        val bookingSets = listOf(1L, 2L).map {
            BookingSet(
                it,
                LocalDateTime.now(),
                estateId = 1L
            )
        }

        //Null-safety
        val companiesFetchedFromSets = bookingSets.map { it.companyId }.map { it?.let { getCompany(it) } ?: throw RuntimeException() }
    }


    @BeforeEach
    fun rebuildDatabase() {
        cleanMigrate()
    }
}

fun createNewBookingInTransaction(input: NewBookingInput): List<Booking> {
    return Database.getDslContext().transactionResult { connection ->
        val dsl = DSL.using(connection)
        val bookingSetId = createBookingSet(companyId = input.companyId, estateId = input.estateId, dslContext = dsl).id
        check(input.amount.times(BigDecimal("100")).remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) == 0) {
            "Only a precision of 2 is supported for amount"
        }
        dsl.insertInto(BOOKING, BOOKING.BOOKING_SET_ID, BOOKING.AMOUNT, BOOKING.ACCOUNT_ID)
            .values(bookingSetId, input.amount, input.debitAccountId)
            .values(bookingSetId, input.amount.negate(), input.creditAccountId)
            .returning()
            .map { it.toBooking() }
    }
}
