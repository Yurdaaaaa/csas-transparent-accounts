package com.zj.core.csastest.net.interceptor

import okhttp3.Interceptor

private const val HEADER_WEB_API_KEY = "WEB-API-key"

class WebApiKeyInterceptor(private val apiKey: String) : Interceptor {

    override fun intercept(chain: Interceptor.Chain) =
        chain.proceed(
            chain.request()
                .newBuilder()
                .header(HEADER_WEB_API_KEY, apiKey)
                .build()
        )
}