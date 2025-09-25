package com.example.material.api.repo

import android.util.Log
import com.example.material.api.AddUserRequest
import com.example.material.api.ApiService
import com.example.material.api.ChatRoomCreateRequest
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

class ChatCreationRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getAllUsers(): List<NonUserResponse> {
        return try {
            val response = apiService.getAllTeachersAndStudents()
            response ?: emptyList()
        } catch (e: Exception) {
            Log.e("ChatCreationRepo", "Error fetching users: ${e.localizedMessage}", e)
            emptyList()
        }
    }
    suspend fun createChatRoom(request: ChatRoomCreateRequest): Result<String> {
        return try {
            Log.d("ChatCreationRepo", "Creating chat room with request: $request")
            val response = apiService.createChatRoom(request)
            if (response.isSuccessful) {
                val message = response.body()?.string() ?: "Success"
                Result.success(message)
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                Result.failure(Exception("Failed: ${response.code()} - $errorMessage"))
            }
        } catch (e: Exception) {
            Log.e("ChatCreationRepo", "Error creating chatroom: ${e.localizedMessage}", e)
            Result.failure(e)
        }
    }



}