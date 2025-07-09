package com.zj.core.csastest.net.model.api

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TransparentAccountsListResponse(
    val pageNumber: Int,
    val pageSize: Int,
    val pageCount: Int,
    val nextPage: Int,
    val recordCount: Int,
    val accounts: List<ApiTransparentAccount> = emptyList(),
    @Transient var isDefaultOfflineSuccessResponse: Boolean = false
)