package com.example.material.api.repo

import android.util.Log
import com.example.material.api.AddUserRequest
import com.example.material.api.ApiService
import com.example.material.api.ClassCreationRequest
import com.example.material.api.ClassDetails
import com.example.material.api.ClassNameResponse
import com.example.material.api.Message
import com.example.material.api.NonUserResponse
import com.example.material.api.PTMRequester
import com.example.material.api.RetrofitClient
import com.example.material.api.StudentResult
import com.example.material.api.UserProfile
import com.example.material.viewmodel.teacher.ResultData
import com.google.gson.JsonSyntaxException
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
    suspend fun createResult(res : ResultData): Response<Message> {
        return api.createResult(res)
    }
    suspend fun getTeacherResults(): List<ResultData> {
        return api.getTeacherResults()
    }

    suspend fun getPtmRequesters(): List<PTMRequester> {
        return try {
            return api.getPtmRequesters()
        } catch (e: JsonSyntaxException) {
            Log.e("PTM", "JsonSyntaxException: The API returned an invalid format for the results.", e)
            emptyList()
        } catch (e: Exception) {
            Log.e("PTM", "An unexpected error occurred while fetching results.", e)
            emptyList()
        }
    }

    suspend fun getPtmRequestersByAttendee(): List<PTMRequester> {
        return try {
            return api.getPtmRequestersByAttendee()
        } catch (e: JsonSyntaxException) {
            Log.e("PTM", "JsonSyntaxException: The API returned an invalid format for the results.", e)
            emptyList()
        } catch (e: Exception) {
            Log.e("PTM", "An unexpected error occurred while fetching results.", e)
            emptyList()
        }
    }

    suspend fun getStudentResults(): List<StudentResult> {
        return try {
            api.getStudentResults()
        } catch (e: JsonSyntaxException) {
            Log.e("StudentResults", "JsonSyntaxException: The API returned an invalid format for the results.", e)
            emptyList()
        } catch (e: Exception) {
            Log.e("StudentResults", "An unexpected error occurred while fetching results.", e)
            emptyList()
        }
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
class UserRepositoryDetailedjwt @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getUserProfile(): Response<UserProfile> {
        return apiService.getUserProfilejwt()
    }
}

//class ClassRepositoryForAddUser @Inject constructor(
//    private val apiService: ApiService
//) {
//    suspend fun addUsersToClass(className: String, users: List<AddUserRequest>) =
//        apiService.addUsersToClass(className, users)
//}