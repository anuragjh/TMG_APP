package com.example.material.api.repo

import android.app.Application
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import com.example.material.api.ApiService
import com.example.material.api.AttendanceList
import com.example.material.api.RoutineEntry
import com.example.material.api.toJsonObject
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import java.io.File
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import org.json.JSONObject
import java.io.FileOutputStream
import java.io.InputStream

@Singleton
class StudentRepo @Inject constructor(
    private val apiService: ApiService,
    private val context: Application
) {

    suspend fun fetchMyAttendance() = apiService.getMyAttendance()


    suspend fun generateAndSendAttendanceEmail(request: AttendanceList): String {
        Log.d("StudentRepo", "Sending attendance report via email...")

        val gson = Gson()
        val jsonString = gson.toJson(request)
        val jsonBody = jsonString.toRequestBody("application/json".toMediaType())

        val response = apiService.generatePdf(jsonBody)

        return if (response.isSuccessful && response.body() != null) {
            response.body()!!.message
        } else {
            val error = response.errorBody()?.string()
            Log.e("StudentRepo", "Email send failed: $error")
            throw IOException("Failed to send email: $error")
        }
    }



    suspend fun fetchRoutine(): List<RoutineEntry> {
        return apiService.getRoutines()
    }

}
