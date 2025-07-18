package com.example.material.viewmodel.teacher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.material.api.EndClassRequest
import com.example.material.api.repo.TeacherClassRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class EndClassUiState {
    object Idle : EndClassUiState()
    object Loading : EndClassUiState()
    object Success : EndClassUiState()
    data class Error(val message: String) : EndClassUiState()
}

@HiltViewModel
class EndClassViewModel @Inject constructor(
    private val repository: TeacherClassRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<EndClassUiState>(EndClassUiState.Idle)
    val uiState: StateFlow<EndClassUiState> = _uiState

    fun endClass(classId: String, topic: String) {
        viewModelScope.launch {
            _uiState.value = EndClassUiState.Loading
            try {
                repository.endClass(classId, EndClassRequest(topic))
                _uiState.value = EndClassUiState.Success
            } catch (e: Exception) {
                e.printStackTrace() // Prints to Logcat
                val message = when (e) {
                    is retrofit2.HttpException -> {
                        val errorBody = e.response()?.errorBody()?.string()
                        "HTTP ${e.code()} - ${e.message()} \n $errorBody"
                    }
                    else -> e.localizedMessage ?: "Something went wrong"
                }
                _uiState.value = EndClassUiState.Error(message)
            }

        }
    }

    fun reset() {
        _uiState.value = EndClassUiState.Idle
    }
}
