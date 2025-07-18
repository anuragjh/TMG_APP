package com.example.material.viewmodel.attendance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.material.api.AttendanceSummary
import com.example.material.api.repo.AttendanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

sealed class AttendanceUiState {
    object Loading : AttendanceUiState()
    data class Success(val grouped: Map<String, List<AttendanceSummary>>) : AttendanceUiState()
    data class Error(val message: String) : AttendanceUiState()
}

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val repo: AttendanceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AttendanceUiState>(AttendanceUiState.Loading)
    val uiState: StateFlow<AttendanceUiState> = _uiState

    fun load() {
        _uiState.value = AttendanceUiState.Loading
        viewModelScope.launch {
            try {
                val list = repo.fetchSummary()
                val grouped = list.groupBy {
                    val date = Date(it.date.seconds * 1000)
                    SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(date).uppercase()
                }
                _uiState.value = AttendanceUiState.Success(grouped)
            } catch (e: Exception) {
                _uiState.value = AttendanceUiState.Error(e.message ?: "Failed")
            }
        }
    }
}
