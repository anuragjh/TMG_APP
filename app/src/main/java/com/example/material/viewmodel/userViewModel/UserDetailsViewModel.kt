package com.example.material.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.material.api.UserProfile
import com.example.material.api.UserProfileUpdateRequest
import com.example.material.api.repo.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import javax.inject.Inject

sealed class UserDetailsUiState {
    object Loading : UserDetailsUiState()
    data class Success(val data: UserProfile) : UserDetailsUiState()
    data class Error(val message: String) : UserDetailsUiState()
}

@HiltViewModel
class UserDetailsViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UserDetailsUiState>(UserDetailsUiState.Loading)
    val uiState: StateFlow<UserDetailsUiState> = _uiState.asStateFlow()

    fun fetchUserDetails(username: String) {
        viewModelScope.launch {
            _uiState.value = UserDetailsUiState.Loading
            val result = repository.fetchUserProfile(username)
            _uiState.value = result.fold(
                onSuccess = { UserDetailsUiState.Success(it) },
                onFailure = { UserDetailsUiState.Error(it.message ?: "Unknown Error") }
            )
        }
    }

    fun updateUser(
        username: String,
        request: UserProfileUpdateRequest,
        onResult: (success: Boolean, message: String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.updateUserProfile(username, request)
            result.onSuccess { responseBody ->
                val message = try {
                    responseBody.string()
                } catch (e: Exception) {
                    "User updated, but response unreadable"
                }
                onResult(true, message)
            }.onFailure { e ->
                onResult(false, e.message ?: "Failed to update user")
            }
        }
    }
    fun deleteUser(
        username: String,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.deleteUser(username)
            result.fold(
                onSuccess = { onSuccess(it) },
                onFailure = { onFailure(it.message ?: "Unknown error") }
            )
        }
    }

}
