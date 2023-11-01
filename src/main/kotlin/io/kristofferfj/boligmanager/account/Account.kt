package io.kristofferfj.boligmanager.account

import io.kristofferfj.Database.getDslContext
import io.kristofferfj.jooq.public_.tables.Account.ACCOUNT
import io.kristofferfj.jooq.public_.tables.records.AccountRecord

data class Account(
    val id: Long,
    val name: String,
    val number: Int,
    val companyId: Long,
)

fun createAccount(name: String, number: Int, companyId: Long): Account {
    return getDslContext().insertInto(ACCOUNT, ACCOUNT.NAME, ACCOUNT.NUMBER, ACCOUNT.COMPANY_ID)
        .values(name, number, companyId)
        .returning()
        .single()
        .toAccount()
}

fun getAccounts(): List<Account> {
    return getDslContext().selectFrom(ACCOUNT).map { it.toAccount() }
}

fun getAccount(id: Long): Account {
    return getDslContext().selectFrom(ACCOUNT).where(ACCOUNT.ID.eq(id)).single().toAccount()
}

fun deleteAccount(accountId: Long): Boolean {
    return getDslContext().deleteFrom(ACCOUNT)
        .where(ACCOUNT.ID.eq(accountId))
        .execute() > 0
}

private fun AccountRecord.toAccount(): Account {
    return Account(
        id = this[ACCOUNT.ID],
        name = this[ACCOUNT.NAME],
        number = this[ACCOUNT.NUMBER],
        companyId = this[ACCOUNT.COMPANY_ID],
    )
}
