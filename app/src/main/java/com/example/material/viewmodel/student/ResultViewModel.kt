// ResultViewModel.kt
package com.example.material.viewmodel.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.material.api.StudentResult
import com.example.material.api.repo.ClassRepositoryForUsers
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Sealed class to represent the UI state
sealed class StudentResultsUiState {
    object Loading : StudentResultsUiState()
    data class Success(val results: List<StudentResult>) : StudentResultsUiState()
    data class Error(val message: String) : StudentResultsUiState()
    object Initial : StudentResultsUiState()
}

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val repository: ClassRepositoryForUsers,
) : ViewModel() {

    private val _uiState = MutableStateFlow<StudentResultsUiState>(StudentResultsUiState.Initial)
    val uiState: StateFlow<StudentResultsUiState> = _uiState.asStateFlow()

    init {
        loadStudentResults()
    }

    fun loadStudentResults() {
        _uiState.value = StudentResultsUiState.Loading
        viewModelScope.launch {
            try {
                val results = repository.getStudentResults()
                _uiState.value = StudentResultsUiState.Success(results)
            } catch (e: Exception) {
                _uiState.value = StudentResultsUiState.Error(e.message ?: "Failed to load results")
            }
        }
    }
}