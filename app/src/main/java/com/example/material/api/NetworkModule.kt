package com.example.material.api

import com.example.material.TokenApi
import com.example.material.datastore.DataStoreManager
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "http://140.245.28.59:8080"
//    private const val BASE_URL = "https://a44c-152-58-182-148.ngrok-free.app"

    @Provides
    @Singleton
    fun provideAuthInterceptor(
        dataStoreManager: DataStoreManager
    ): Interceptor = AuthInterceptor {
        runBlocking { dataStoreManager.getToken() }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(interceptor: Interceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .build()
    }
    /* <-- ADD THIS ------------------------------------------------------------------- */
    @Provides
    @Singleton
    fun provideTokenApi(retrofit: Retrofit): TokenApi =
        retrofit.create(TokenApi::class.java)
    /* -------------------------------------------------------------------------------- */

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory()) // ✅ Required for Kotlin data classes
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)
}