package com.example.material.viewmodel.student

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.material.api.ApiService
import com.example.material.api.AttendanceList
import com.example.material.api.repo.StudentRepo
import com.example.material.datastore.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    private val _pdfLoading = MutableStateFlow(false)
    val pdfLoading: StateFlow<Boolean> = _pdfLoading.asStateFlow()

    fun viewFullReportAndShowMessage(
        attendanceList: AttendanceList,
        onResult: (String?) -> Unit
    ) {
        viewModelScope.launch {
            _pdfLoading.value = true
            try {
                val resultMessage = repo.generateAndSendAttendanceEmail(attendanceList)
                onResult(resultMessage)
            } catch (e: Exception) {
                Log.e("StudentReportVM", "Sending attendance email failed: ${e.localizedMessage}")
                onResult("Failed to send report. Please try again.")
            } finally {
                _pdfLoading.value = false
            }
        }
    }





}
