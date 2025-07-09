package com.zj.core.csastest.net.interceptor

import okhttp3.Interceptor

private const val HEADER_USER_AGENT = "User-Agent"

class UserAgentInterceptor(private val userAgent: String) : Interceptor {

    override fun intercept(chain: Interceptor.Chain) =
        chain.proceed(
            chain.request()
                .newBuilder()
                .header(HEADER_USER_AGENT, userAgent)
                .build()
        )
}
