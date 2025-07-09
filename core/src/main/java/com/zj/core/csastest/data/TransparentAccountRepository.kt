package com.zj.core.csastest.data

import app.cash.sqldelight.rx2.asObservable
import app.cash.sqldelight.rx2.mapToList
import com.zj.core.csastest.data.model.TransparentAccount
import com.zj.core.csastest.data.model.TransparentAccountQueries
import com.zj.core.csastest.util.LOG

typealias DbTransparentAccount = TransparentAccount
typealias DbTransparentAccountImpl = TransparentAccount // to know where it is initialized

class TransparentAccountRepository(private val transparentAccountQueries: TransparentAccountQueries) {

    fun transparentAccountsChangedObservable() = transparentAccountQueries.transparentAccountsChanged()
        .asObservable()
        .map { Unit }

    fun getAllTransparentAccountsSorted() = transparentAccountQueries.selectAllSortedByDate().asObservable().mapToList()

    fun insertOrUpdateTransparentAccounts(
        accounts: List<DbTransparentAccount>
    ) {
        return transparentAccountQueries.transaction {
            accounts.forEach {
                insertOrUpdateTransparentAccount(it)
            }
        }
    }

    private fun insertOrUpdateTransparentAccount(account: DbTransparentAccount): Boolean {
        return transparentAccountQueries.transactionWithResult {
            updateAccount(account)
            if (transparentAccountQueries.changes().executeAsOne() == 0L) {
                insertTransparentAccount(account)
                LOG.d("TransparentAccountRepository inserting transparentAccount: $account")
                true
            } else {
                LOG.d("TransparentAccountRepository updated transparentAccount: $account")
                false
            }
        }
    }

    private fun insertTransparentAccount(account: DbTransparentAccount) {
        transparentAccountQueries.insertAccount(
            accountNumber = account.accountNumber,
            bankCode = account.bankCode,
            transparencyFrom = account.transparencyFrom,
            transparencyTo = account.transparencyTo,
            publicationTo = account.publicationTo,
            actualizationDate = account.actualizationDate,
            balance = account.balance,
            currency = account.currency,
            name = account.name,
            iban = account.iban,
        )
    }

    private fun updateAccount(account: DbTransparentAccount) {
        transparentAccountQueries.update(
            bankCode = account.bankCode,
            transparencyFrom = account.transparencyFrom,
            transparencyTo = account.transparencyTo,
            publicationTo = account.publicationTo,
            actualizationDate = account.actualizationDate,
            balance = account.balance,
            currency = account.currency,
            name = account.name,
            iban = account.iban,
            accountNumber = account.accountNumber
        )
    }

    fun clear() {
        transparentAccountQueries.deleteAll()
    }
}