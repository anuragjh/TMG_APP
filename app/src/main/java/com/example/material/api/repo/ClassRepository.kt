package com.example.material.api.repo

import com.example.material.api.ApiService
import com.example.material.api.ClassCreationRequest
import javax.inject.Inject

class ClassRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun createClass(request: ClassCreationRequest): Result<String> {
        return try {
            val res = apiService.createClass(request)
            if (res.isSuccessful) {
                val body = res.body()
                if (body != null) {
                    Result.success(body.message)
                } else {
                    Result.failure(Exception("Empty response"))
                }
            } else {
                Result.failure(Exception(res.errorBody()?.string() ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
