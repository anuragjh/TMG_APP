package com.example.material.viewmodel.teacher

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.material.api.ChatRoomCreateRequest
import com.example.material.api.NonUserResponse
import com.example.material.api.repo.ChatRoomRepository
import com.example.material.datastore.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import javax.inject.Inject

@HiltViewModel
class ChatUpdationViewModel @Inject constructor(
    private val repository: ChatRoomRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserListUiState())
    val uiState: StateFlow<UserListUiState> = _uiState.asStateFlow()

    private val _selectedUsernames = MutableStateFlow<Set<String>>(emptySet())
    val selectedUsernames: StateFlow<Set<String>> = _selectedUsernames.asStateFlow()

    private val _currentUsername = MutableStateFlow<String?>(null)
    val currentUsername: StateFlow<String?> = _currentUsername.asStateFlow()

    private val _chatUpdateResult = MutableStateFlow<Result<ResponseBody>?>(null)
    val chatUpdateResult: StateFlow<Result<ResponseBody>?> = _chatUpdateResult.asStateFlow()

    fun loadUsername(context: Context) {
        viewModelScope.launch {
            val username = DataStoreManager(context).getUsername()
            _currentUsername.value = username
        }
    }

    fun loadUsersForClass(className: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val allUsers = repository.getAllUsers()
                Log.d("ChatUpdationViewModel", "All users: $allUsers")
                val classUsernames = repository.getAllUsersPartOfClass(className).toSet()
                Log.d("ChatUpdationViewModel", "Class usernames: $classUsernames")

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    users = allUsers
                )
                _selectedUsernames.value = classUsernames
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.localizedMessage ?: "Failed to load users"
                )
            }
        }
    }

    fun updateChatRoom(
        className: String,
        students: List<String>,
        teachers: List<String>,
        everyone: Boolean
    ) {
        viewModelScope.launch {
            try {
                // Create the request body using ChatRoomCreateRequest
                val request = ChatRoomCreateRequest(
                    className = className,
                    teachers = teachers,
                    students = students,
                    canEveryoneMessage = everyone
                )

                val response = repository.updateChatroom(className, request)
                if (response.isSuccessful) {
                    _chatUpdateResult.value = Result.success(response.body()!!)
                } else {
                    _chatUpdateResult.value = Result.failure(Exception("API Error: ${response.code()}"))
                }
            } catch (e: Exception) {
                Log.e("ChatUpdationViewModel", "Error updating chatroom: ${e.localizedMessage}", e)
                _chatUpdateResult.value = Result.failure(e)
            }
        }
    }

    fun resetUpdateResult() {
        _chatUpdateResult.value = null
    }
}

