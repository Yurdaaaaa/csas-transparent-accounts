package com.zj.core.csastest.data.model

import java.math.BigDecimal

data class TransparentAccountDomain(
    val accountNumber: String,
    val bankCode: String,
    val transparencyFrom: String,
    val transparencyTo: String,
    val publicationTo: String,
    val actualizationDate: String,
    val balance: BigDecimal,
    val currency: String,
    val name: String,
    val iban: String,
)

fun TransparentAccount.toDomain(): TransparentAccountDomain {
    return TransparentAccountDomain(
        accountNumber = accountNumber,
        bankCode = bankCode,
        transparencyFrom = transparencyFrom,
        transparencyTo = transparencyTo,
        publicationTo = publicationTo,
        actualizationDate = actualizationDate,
        balance = balance.toBigDecimalOrNull() ?: BigDecimal.ZERO,
        currency = currency,
        name = name,
        iban = iban
    )
}