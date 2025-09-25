package com.example.material.viewmodel.teacher

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.material.api.ChatRoomCreateRequest
import com.example.material.api.NonUserResponse
import com.example.material.api.repo.ChatCreationRepository
import com.example.material.datastore.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserListUiState(
    val isLoading: Boolean = false,
    val users: List<NonUserResponse> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class ChatCreationViewModel @Inject constructor(
    private val repository: ChatCreationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserListUiState())
    val uiState: StateFlow<UserListUiState> = _uiState.asStateFlow()

    private val _currentUsername = MutableStateFlow<String?>(null)
    val currentUsername: StateFlow<String?> = _currentUsername

    fun loadUsername(context: Context) {
        viewModelScope.launch {
            val username = DataStoreManager(context).getUsername()
            _currentUsername.value = username
        }
    }

    init {
        fetchUsers()
    }

    fun fetchUsers() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val users = repository.getAllUsers()
                _uiState.value = _uiState.value.copy(isLoading = false, users = users)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.localizedMessage ?: "An unknown error occurred"
                )
            }
        }
    }

    private val _chatCreateResult = MutableStateFlow<Result<String>?>(null)
    val chatCreateResult: StateFlow<Result<String>?> = _chatCreateResult

    fun createChatRoom(className: String, students: List<String>, teachers: List<String>, everyone: Boolean) {
        viewModelScope.launch {
            val result = repository.createChatRoom(ChatRoomCreateRequest(className, students, teachers, everyone))
            _chatCreateResult.value = result
        }
    }
}