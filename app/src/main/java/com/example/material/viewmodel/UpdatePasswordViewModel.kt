package com.example.material.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.material.api.ApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpdatePasswordViewModel @Inject constructor(
    private val repository: ApiRepository.PasswordRepository
) : ViewModel() {

    sealed class UiState {
        object Idle : UiState()
        object Loading : UiState()
        object Success : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _state = MutableStateFlow<UiState>(UiState.Idle)
    val state: StateFlow<UiState> = _state

    fun updatePassword(key: String, password: String) {
        _state.value = UiState.Loading
        viewModelScope.launch {
            val result = repository.updatePassword(key, password)
            _state.value = result.fold(
                onSuccess = { UiState.Success },
                onFailure = { UiState.Error(it.message ?: "Unknown Error") }
            )
        }
    }

    fun resetState() {
        _state.value = UiState.Idle
    }
}
