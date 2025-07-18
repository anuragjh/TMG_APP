// AuthRepository.kt
package com.example.material.api.repo


import com.example.material.api.ApiService
import com.example.material.api.CreateUserRequest
import com.example.material.api.CreateUserResponse
import com.example.material.api.User
import com.example.material.api.UserProfile
import com.example.material.api.UserProfileUpdateRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Response
import javax.inject.Inject
import okhttp3.ResponseBody

class UserRepository @Inject constructor(
    private val api: ApiService
) {
    suspend fun createUser(body: CreateUserRequest): Result<CreateUserResponse> {
        return try {
            val response = api.createUser(body)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun fetchAllUsers(): Result<List<User>> = runCatching {
        api.getAllUsers()
    }
    suspend fun fetchUserProfile(username: String): Result<UserProfile> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.getUserProfile(username)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("User not found or API failed"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    suspend fun updateUserProfile(username: String, request: UserProfileUpdateRequest): Result<ResponseBody> {
        return try {
            val response = api.updateUserProfile(username, request)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Update failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun deleteUser(username: String): Result<String> {
        return try {
            val response = api.deleteUser(username)
            if (response.isSuccessful) {
                Result.success(response.body()?.string() ?: "User deleted.")
            } else {
                Result.failure(Exception("Delete failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
