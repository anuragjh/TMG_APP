package com.example.material.viewmodel.teacher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.material.api.repo.CallRepo
import com.example.material.api.UserWithPhn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserListUiStates(
    val users: List<UserWithPhn> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class CallViewModel @Inject constructor(
    private val callRepo: CallRepo
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserListUiStates(isLoading = true))
    val uiState: StateFlow<UserListUiStates> = _uiState.asStateFlow()

    init {
        fetchUsers()
    }

    private fun fetchUsers() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val users = callRepo.getAllUsers()
                _uiState.value = _uiState.value.copy(
                    users = users,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to fetch users: ${e.localizedMessage}"
                )
            }
        }
    }
}