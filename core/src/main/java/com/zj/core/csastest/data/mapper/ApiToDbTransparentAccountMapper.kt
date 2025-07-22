package com.zj.core.csastest.data.mapper

import com.zj.core.csastest.data.DbTransparentAccountImpl
import com.zj.core.csastest.net.model.api.ApiTransparentAccount
import java.math.BigDecimal

class ApiToDbTransparentAccountMapper {

    fun mapApiAccountToDbAccount(apiAccount: ApiTransparentAccount): DbTransparentAccountImpl {
        return DbTransparentAccountImpl(
            accountNumber = apiAccount.accountNumber,
            bankCode = apiAccount.bankCode,
            transparencyFrom = apiAccount.transparencyFrom,
            transparencyTo = apiAccount.transparencyTo,
            publicationTo = apiAccount.publicationTo,
            actualizationDate = apiAccount.actualizationDate,
            balance = BigDecimal.valueOf(apiAccount.balance).toString(), // from API should come string instead of double?
            currency = apiAccount.currency,
            name = apiAccount.name.trim(), // haha, i have almost missed this catch with extra space (centrum sebevedome cesko z.u) !
            iban = apiAccount.iban
        )
    }
}