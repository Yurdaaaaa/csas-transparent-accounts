package com.zj.core.csastest.net

import com.zj.core.csastest.net.model.api.TransparentAccountsListResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface TransparentAccountsApi {

    @GET("transparentAccounts/")
    fun getTransparentAccountsList(
        @Query("page") page: Int, // starts from 0
        @Query("size") size: Int,
        @Query("filter") filter: String? = null
    ): Observable<TransparentAccountsListResponse>


}