package com.example.material.api

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response
import okhttp3.ResponseBody
import retrofit2.http.Query

data class LoginRequest(val username: String, val password: String)
data class LoginResponse(
    val jwt: String?,
    val role: String?
)

interface ApiService {

    @POST("/api/auth/login")
    suspend fun login(@Body body: LoginRequest): Response<LoginResponse>



    @POST("/api/auth/forgot-password")
    suspend fun forgotPassword(@Query("email") email: String): Response<ResponseBody>

    @POST("api/auth/verify-otp")
    suspend fun verifyOtp(
        @Query("email") email: String,
        @Query("otp") otp: String
    ): Response<Map<String, String>>


}
