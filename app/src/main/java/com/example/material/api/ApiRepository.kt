package com.example.material.api

import com.example.material.datastore.DataStoreManager
import com.example.material.pages.admin.User
import javax.inject.Inject
import okhttp3.ResponseBody
import retrofit2.Response


class ApiRepository(private val api: ApiService) {

    suspend fun login(username: String, password: String): Result<LoginResponse> {
        return try {
            val res = api.login(LoginRequest(username, password))
            val body = res.body()
            if (res.isSuccessful && body?.jwt != null && body.role != null) {
                Result.success(body)
            } else {
                Result.failure(Exception("Invalid credentials or missing fields"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun forgotPassword(email: String): Result<String> {
        return try {
            val res = api.forgotPassword(email)
            if (res.isSuccessful && res.body() != null) {
                val message = res.body()!!.string()
                Result.success(message)
            } else {
                Result.failure(Exception("Something went wrong"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    class OTPRepository  @Inject constructor(private val api: ApiService) {
        suspend fun verifyOtp(email: String, otp: String): Result<String> {
            return try {
                val response = api.verifyOtp(email, otp)
                if (response.isSuccessful) {
                    val key = response.body()?.get("key") ?: return Result.failure(Exception("No key in response"))
                    Result.success(key)
                } else {
                    Result.failure(Exception("Invalid OTP"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }


    class PasswordRepository @Inject constructor(
        private val api: ApiService
    ) {
        suspend fun updatePassword(key: String, newPassword: String): Result<String> {
            return try {
                val response: Response<ResponseBody> = api.updatePassword(UpdatePasswordRequest(key, newPassword))
                if (response.isSuccessful) {
                    Result.success(response.body()?.string() ?: "Password updated successfully.")
                } else {
                    Result.failure(Exception("Failed to update password"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }


    class UserRepository @Inject constructor(
        private val apiService: ApiService,
        private val dataStoreManager: DataStoreManager
    ) {
        suspend fun getUsers(role: String): List<User> {
            val response = apiService.getAllUsers(role)
            return response.map { User(name = it.name, username = it.username) }
        }
    }



    // Add more functions (forgotPassword, getUserData, etc.)
}
