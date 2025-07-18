// TakeAttendanceViewModel.kt
package com.example.material.viewmodel.teacher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.material.api.AttendanceRequest
import com.example.material.api.Student
import com.example.material.api.repo.TeacherClassRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AttendanceUiState {
    object Loading : AttendanceUiState()
    data class Success(val students: List<Student>) : AttendanceUiState()
    data class Error(val message: String) : AttendanceUiState()
    object Submitted : AttendanceUiState()
}

@HiltViewModel
class TakeAttendanceViewModel @Inject constructor(
    private val repository: TeacherClassRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AttendanceUiState>(AttendanceUiState.Loading)
    val uiState: StateFlow<AttendanceUiState> = _uiState

    fun loadStudents(classId: String) {
        _uiState.value = AttendanceUiState.Loading
        viewModelScope.launch {
            try {
                val result = repository.fetchStudents(classId)
                _uiState.value = AttendanceUiState.Success(result)
            } catch (e: Exception) {
                _uiState.value = AttendanceUiState.Error(e.message ?: "Something went wrong")
            }
        }
    }

    fun submitAttendance(classId: String, attendanceMap: Map<String, Boolean>) {
        viewModelScope.launch {
            try {
                val present = attendanceMap.filterValues { it }.keys.toList()
                val absent = attendanceMap.filterValues { !it }.keys.toList()
                val response = repository.submitAttendance(
                    classId,
                    AttendanceRequest(present, absent)
                )
                _uiState.value = AttendanceUiState.Submitted
            } catch (e: Exception) {
                _uiState.value = AttendanceUiState.Error(e.message ?: "Failed to submit attendance")
            }
        }
    }

}
