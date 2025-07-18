package com.example.material.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "http://140.245.28.59:8080"

    private var tokenProvider: () -> String? = { null }

    fun setTokenProvider(provider: () -> String?) {
        tokenProvider = provider
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor { tokenProvider() })
        .build()

    // ✅ Expose the Retrofit instance for reuse
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}

