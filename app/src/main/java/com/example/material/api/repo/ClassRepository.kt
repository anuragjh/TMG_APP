package com.example.material.api.repo

import com.example.material.api.AddUserRequest
import com.example.material.api.ApiService
import com.example.material.api.ClassCreationRequest
import com.example.material.api.ClassDetails
import com.example.material.api.ClassNameResponse
import com.example.material.api.NonUserResponse
import com.example.material.api.RetrofitClient
import okhttp3.ResponseBody
import retrofit2.Response
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


class ClassRepositoryByName @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getAllClasses(): List<ClassNameResponse> {
        return apiService.getClassNames()
    }
}

class ClassRepositoryForNonUsers @Inject constructor(
    private val api: ApiService
) {
    suspend fun fetchNonUsers(className: String): List<NonUserResponse> {
        return api.getNonUsers(className)
    }
    suspend fun addUsersToClass(
        className: String,
        users: List<AddUserRequest>
    ): Response<ResponseBody> {
        return api.addUsersToClass(className, users)
    }

}

class ClassRepositoryForUsers @Inject constructor(
    private val api: ApiService
) {
    suspend fun fetchNonUsers(className: String): List<NonUserResponse> {
        return api.getUsers(className)
    }
    suspend fun removeUsersFromClass(
        className: String,
        users: List<AddUserRequest>
    ): Response<ResponseBody> {
        return api.removeUsersFromClass(className, users)
    }

}


class ClassRepositoryForDetails @Inject constructor(private val apiService: ApiService) {
    suspend fun getClassDetails(className: String): ClassDetails {
        return apiService.getClassDetails(className)
    }
    suspend fun deleteClass(className: String): Boolean {
        val response = apiService.deleteClass(className)
        return response.isSuccessful
    }
}

//class ClassRepositoryForAddUser @Inject constructor(
//    private val apiService: ApiService
//) {
//    suspend fun addUsersToClass(className: String, users: List<AddUserRequest>) =
//        apiService.addUsersToClass(className, users)
//}