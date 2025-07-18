package com.example.material.viewmodel.common


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.material.api.UserProfile
import com.example.material.api.repo.UserRepositoryDetailedjwt
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ProfileUiState {
    object Loading : ProfileUiState()
    data class Success(val profile: UserProfile) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: UserRepositoryDetailedjwt
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState

    init {
        fetchProfile()
    }

    private fun fetchProfile() {
        viewModelScope.launch {
            try {
                val response = repository.getUserProfile()
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = ProfileUiState.Success(response.body()!!)
                } else {
                    _uiState.value = ProfileUiState.Error("Failed to load profile")
                }
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
