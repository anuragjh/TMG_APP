package com.example.material.api

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val tokenProvider: () -> String?
) : Interceptor {

    private val noAuthPaths = listOf(
        "/api/auth/login",
        "/api/auth/forgot-password",
        "/api/auth/new-password",
        "/api/auth/verify-otp"
    )

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath

        val shouldAddToken = noAuthPaths.none { path.contains(it) }

        val updatedRequest = if (shouldAddToken) {
            val token = tokenProvider()
            if (!token.isNullOrEmpty()) {
                request.newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
            } else {
                request
            }
        } else {
            request
        }

        return chain.proceed(updatedRequest)
    }
}
