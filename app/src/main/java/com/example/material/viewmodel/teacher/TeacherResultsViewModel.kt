package com.example.material.viewmodel.teacher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.material.api.repo.ClassRepositoryForUsers
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


// State for the results list screen
sealed class ResultsListUiState {
    object Loading : ResultsListUiState()
    data class Success(val results: List<ResultData>) : ResultsListUiState()
    data class Error(val message: String) : ResultsListUiState()
    object Initial : ResultsListUiState()
}

@HiltViewModel
class TeacherResultsViewModel @Inject constructor(
    private val repository: ClassRepositoryForUsers,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ResultsListUiState>(ResultsListUiState.Initial)
    val uiState: StateFlow<ResultsListUiState> = _uiState.asStateFlow()

    fun loadTeacherResults() {
        _uiState.value = ResultsListUiState.Loading
        viewModelScope.launch {
            try {
                val results = repository.getTeacherResults()
                _uiState.value = ResultsListUiState.Success(results)
            } catch (e: Exception) {
                _uiState.value = ResultsListUiState.Error(e.message ?: "Failed to load results")
            }
        }
    }
}