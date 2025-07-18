package com.example.material.viewmodel.teacher

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.material.api.ClassNameResponse
import com.example.material.api.repo.TeacherClassRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ClassListUiState {
    object Loading : ClassListUiState()
    data class Success(val classes: List<ClassNameResponse>) : ClassListUiState()
    data class Error(val message: String) : ClassListUiState()
}

@HiltViewModel
class StartClassViewModel @Inject constructor(
    private val classRepository: TeacherClassRepository
) : ViewModel() {

    // Class list UI state
    private val _uiState = mutableStateOf<ClassListUiState>(ClassListUiState.Loading)
    val uiState: State<ClassListUiState> = _uiState

    // Class start response state
    private val _startStatus = mutableStateOf<String?>(null)
    val startStatus: State<String?> = _startStatus

    fun loadClasses() {
        viewModelScope.launch {
            _uiState.value = ClassListUiState.Loading
            try {
                val data = classRepository.getMyClasses()
                _uiState.value = ClassListUiState.Success(data)
            } catch (e: Exception) {
                _uiState.value = ClassListUiState.Error("Failed to load classes: ${e.message}")
            }
        }
    }

    private val _isStarting = mutableStateOf(false)
    val isStarting: State<Boolean> = _isStarting

    fun startClass(className: String) {
        viewModelScope.launch {
            _isStarting.value = true
            try {
                val classCode = classRepository.startClass(className)
                _startStatus.value = classCode  // success result
            } catch (e: Exception) {
                _startStatus.value = "ERROR: ${e.message}"
            } finally {
                _isStarting.value = false
            }
        }
    }

    fun clearStartStatus() {
        _startStatus.value = null
    }
}
