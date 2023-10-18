package io.kristofferfj.boligmanager.account

import io.kristofferfj.Database
import io.kristofferfj.jooq.public_.tables.Account.ACCOUNT
import io.kristofferfj.jooq.public_.tables.records.AccountRecord

data class Account(
    val id: Long,
    val name: String,
    val number: Int,
    val companyId: Long,
)

fun createAccount(name: String, number: Int, companyId: Long): Account {
    val dsl = Database.connect()
    val accountId = dsl.insertInto(ACCOUNT, ACCOUNT.NAME, ACCOUNT.NUMBER, ACCOUNT.COMPANY_ID)
        .values(name, number, companyId)
        .returning(ACCOUNT.ID)
        .fetchOne()
        ?.getValue(ACCOUNT.ID)

    return Account(id = accountId!!, name = name, number = number, companyId = companyId)
}

fun getAccounts(): List<Account> {
    return Database.connect().selectFrom(ACCOUNT).map { it.toAccount() }
}

fun getAccount(id: Long): Account {
    return Database.connect().selectFrom(ACCOUNT).where(ACCOUNT.ID.eq(id)).single().toAccount()
}

fun deleteAccount(accountId: Long): Boolean {
    return Database.connect().deleteFrom(ACCOUNT)
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
