package com.example.material.viewmodel.student

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.material.api.ApiService
import com.example.material.api.repo.StudentRepo
import com.example.material.datastore.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import javax.inject.Inject

sealed class AttendanceUiState {
    object Loading : AttendanceUiState()
    data class Success(val data: ApiService.MyAttendanceResponse) : AttendanceUiState()
    data class Error(val message: String) : AttendanceUiState()
}

@HiltViewModel
class StudentViewModel @Inject constructor(
    private val repo: StudentRepo
) : ViewModel() {
    private val _uiState = MutableStateFlow<AttendanceUiState>(AttendanceUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init { loadAttendance() }

    fun loadAttendance() = viewModelScope.launch {
        _uiState.value = AttendanceUiState.Loading
        runCatching { repo.fetchMyAttendance() }
            .onSuccess { _uiState.value = AttendanceUiState.Success(it) }
            .onFailure { _uiState.value = AttendanceUiState.Error(it.localizedMessage ?: "Error loading") }
    }
    fun Context.viewPdfReport(attendance: List<ApiService.AttendanceEntry>) {
        CoroutineScope(Dispatchers.IO).launch {
            val dataStoreManager = DataStoreManager(applicationContext)
            val token = dataStoreManager.getToken()
            try {
                val client = OkHttpClient()

                val jsonBody = JSONObject().apply {
                    put("attendance", JSONArray(attendance.map { it.toJsonObject() }))
                }

                val request = Request.Builder()
                    .url("http://140.245.28.59:8080/api/generate-pdf")
                    .addHeader("Authorization", "Bearer $token")
                    .post(RequestBody.create("application/json".toMediaType(), jsonBody.toString()))
                    .build()

                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val inputStream = response.body?.byteStream()

                    // Save PDF locally
                    val pdfFile = File(cacheDir, "attendance_report.pdf")
                    pdfFile.outputStream().use { output -> inputStream?.copyTo(output) }

                    // Open with built-in PDF viewer
                    val uri = FileProvider.getUriForFile(
                        this@viewPdfReport,
                        "${packageName}.fileprovider",
                        pdfFile
                    )

                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(uri, "application/pdf")
                        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
                    }

                    startActivity(intent)
                } else {
                    Log.e("PDF", "Server error: ${response.code}")
                }
            } catch (e: Exception) {
                Log.e("PDF", "Error: ${e.message}", e)
            }
        }
    }


}
