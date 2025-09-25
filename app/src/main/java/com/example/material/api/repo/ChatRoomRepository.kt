package com.example.material.api.repo

import android.util.Log
import com.example.material.api.ApiService
import com.example.material.api.ChatRoomCreateRequest
import com.example.material.api.NonUserResponse
import okhttp3.ResponseBody
import org.json.JSONArray
import retrofit2.Response
import javax.inject.Inject

class ChatRoomRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getMyCreatedChatRooms(): Response<ResponseBody> {
        return apiService.getMyCreatedChatRooms()
    }

    suspend fun deleteClassChat(className: String): Response<ResponseBody> {
        return apiService.deleteClassChat(className)
    }

    suspend fun getAllUsers(): List<NonUserResponse> {
        return try {
            val response = apiService.getAllTeachersAndStudents()
            response ?: emptyList()
        } catch (e: Exception) {
            Log.e("ChatCreationRepo", "Error fetching users: ${e.localizedMessage}", e)
            emptyList()
        }
    }
    suspend fun getAllUsersPartOfClass(className: String): List<String> {
        return try {
            val response = apiService.allChatUsers(className)
            if (response.isSuccessful && response.body() != null) {
                val bodyString = response.body()!!.string()
                val jsonArray = JSONArray(bodyString)
                List(jsonArray.length()) { index -> jsonArray.getString(index) }
            } else {
                Log.e("ChatRoomRepo", "API call failed: ${response.code()} - ${response.message()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("ChatRoomRepo", "Error fetching class users: ${e.localizedMessage}", e)
            emptyList()
        }
    }

    suspend fun updateChatroom(className: String, request: ChatRoomCreateRequest): Response<ResponseBody> {
        return apiService.updateChatRoom(className, request)
    }


}
