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
import com.example.material.api.UserWithPhn
import com.example.material.viewmodel.teacher.ResultData
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

class CallRepo @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun getAllUsers(): List<UserWithPhn> {
        return try {
            val response = apiService.getAllTeachersAndStudentswPhn()

            if (response.isSuccessful) {
                val body = response.body()?.string()
                Log.d("CallRepo", "Response body: $body")
                if (!body.isNullOrBlank()) {
                    Gson().fromJson(body, object : TypeToken<List<UserWithPhn>>() {}.type)
                } else emptyList()
            } else {
                Log.e("CallRepo", "Error fetching users: ${response.errorBody()?.string()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("CallRepo", "Error fetching users: ${e.localizedMessage}", e)
            emptyList()
        }
    }

}