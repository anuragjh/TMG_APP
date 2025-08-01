package com.example.material.viewmodel.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.material.api.PTMRequester
import com.example.material.api.repo.ClassRepositoryForUsers
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface PtmUiState {
    object Loading : PtmUiState
    data class Success(val ptmRequests: List<PTMRequester>) : PtmUiState
    data class Error(val message: String) : PtmUiState
}

@HiltViewModel
class PTMViewModelStd @Inject constructor(
    private val repository: ClassRepositoryForUsers
) : ViewModel() {

    private val _uiState = MutableStateFlow<PtmUiState>(PtmUiState.Loading)
    val uiState: StateFlow<PtmUiState> = _uiState.asStateFlow()

    init {
        fetchPtmRequestsByAttendee()
    }

    fun fetchPtmRequestsByAttendee() {
        viewModelScope.launch {
            _uiState.value = PtmUiState.Loading
            try {
                // Assuming repository.getPtmRequestersByAttendee() is your API call
                val requests = repository.getPtmRequestersByAttendee()
                _uiState.value = PtmUiState.Success(requests)
            } catch (e: Exception) {
                _uiState.value = PtmUiState.Error("Failed to fetch PTM requests. Please try again.")
            }
        }
    }
}