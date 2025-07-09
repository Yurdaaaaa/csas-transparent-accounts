package com.zj.core.csastest.net

import com.zj.core.csastest.net.model.api.TransparentAccountsListResponse
import io.reactivex.Observable

const val DEFAULT_ACCOUNTS_PAGE_SIZE = 50
const val SHORT_ACCOUNTS_PAGE_SIZE = 20

class TransparentAccountsApiClient(private val api: TransparentAccountsApi) {

    fun getTransparentAccountList(page: Int, size: Int = DEFAULT_ACCOUNTS_PAGE_SIZE, filter: String? = null): Observable<TransparentAccountsListResponse> {
        return api.getTransparentAccountsList(
            page = page,
            size = size,
            filter = filter
        )
    }
}