package com.faris.currency.interceptors

import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class HttpRequestInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original: Request = chain.request()
        val originalHttpUrl: HttpUrl = original.url

        val url: HttpUrl = originalHttpUrl.newBuilder()
            .addQueryParameter("access_key", API_KEY)
            .build()

        // Request customization: add request headers

        // Request customization: add request headers
        val requestBuilder: Request.Builder = original.newBuilder()
            .url(url)

        val request: Request = requestBuilder.build()
        return chain.proceed(request)
    }

    companion object {
        private const val API_KEY = "e6f3f88042886f7521b3b5f226db128f"
    }
}