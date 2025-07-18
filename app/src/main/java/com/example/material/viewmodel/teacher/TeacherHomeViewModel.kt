package com.example.material.viewmodel.teacher

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.material.api.OngoingClass
import com.example.material.api.repo.TeacherClassRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

sealed class TeacherHomeUiState {
    object Idle : TeacherHomeUiState()
    object Loading : TeacherHomeUiState()
    data class Success(val classes: List<OngoingClass>) : TeacherHomeUiState()
    data class Error(val message: String) : TeacherHomeUiState()
}

@HiltViewModel
class TeacherHomeViewModel @Inject constructor(
    private val repository: TeacherClassRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<TeacherHomeUiState>(TeacherHomeUiState.Idle)
    val uiState: StateFlow<TeacherHomeUiState> = _uiState

    private var hasLoaded = false

    suspend fun getUsernameFromApi(): Response<ResponseBody>? {
        return try {
            repository.getUsernameFromApi()
        } catch (e: Exception) {
            Log.e("AuthVM", "❌ Failed to fetch username", e)
            null
        }
    }

    fun loadClasses() {
        if (hasLoaded) return
        _uiState.value = TeacherHomeUiState.Loading
        viewModelScope.launch {
            try {
                val result = repository.fetchOngoingClasses()
                _uiState.value = TeacherHomeUiState.Success(result)
                hasLoaded = true
            } catch (e: Exception) {
                _uiState.value = TeacherHomeUiState.Error(e.message ?: "Something went wrong")
            }
        }
    }

    fun refreshClasses() {
        _uiState.value = TeacherHomeUiState.Loading
        viewModelScope.launch {
            try {
                val result = repository.fetchOngoingClasses()
                _uiState.value = TeacherHomeUiState.Success(result)
                hasLoaded = true
            } catch (e: Exception) {
                _uiState.value = TeacherHomeUiState.Error(e.message ?: "Something went wrong")
            }
        }
    }
}
