package com.zj.core.csastest.net.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.util.*

private const val HEADER = "X-Idempotency-Key"

class IdempotencyInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        return chain.proceed(
            if (request.method == "POST") {
                request
                    .newBuilder()
                    .header(HEADER, UUID.randomUUID().toString())
                    .build()
            } else {
                request
            }
        )
    }
}