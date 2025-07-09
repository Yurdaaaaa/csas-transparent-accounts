package com.zj.core.csastest.net.model.api

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiTransparentAccount(
    val accountNumber: String,
    val bankCode: String,
    val transparencyFrom: String,   // api -> db ISO-8601 date-time is sortable as TEXT in SQL; from db->domain TEXT(jsr310) convert to localDateTime
    val transparencyTo: String,
    val publicationTo: String,
    val actualizationDate: String,
    val balance: Double,
    val currency: String = "",
    val name: String,
    val iban: String
)