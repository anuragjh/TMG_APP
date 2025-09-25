package com.example.material.viewmodel.teacher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.material.api.repo.ChatRoomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ChatRoomViewModel @Inject constructor(
    private val repository: ChatRoomRepository
) : ViewModel() {

    private val _chatRoomIds = MutableStateFlow<List<String>>(emptyList())
    val chatRoomIds: StateFlow<List<String>> = _chatRoomIds

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // New StateFlow for toast messages
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage

    fun fetchMyCreatedChatRooms() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = repository.getMyCreatedChatRooms()
                if (response.isSuccessful) {
                    val body = response.body()?.string()
                    if (body != null) {
                        try {
                            val ids = Gson().fromJson(body, Array<String>::class.java)?.toList() ?: emptyList()
                            _chatRoomIds.value = ids
                        } catch (e: JsonSyntaxException) {
                            _errorMessage.value = "Failed to parse server response."
                        }
                    } else {
                        _errorMessage.value = "Empty response from server."
                    }
                } else {
                    _errorMessage.value = "Failed: ${response.code()} ${response.message()}"
                }
            } catch (e: IOException) {
                _errorMessage.value = "Network error: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteChatRoom(className: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = repository.deleteClassChat(className)
                if (response.isSuccessful) {
                    // Update the list by removing the deleted chat room
                    _chatRoomIds.value = _chatRoomIds.value.filter { it != className }
                    _toastMessage.value = "Class chat '$className' deleted successfully."
                } else {
                    _errorMessage.value = "Failed to delete class chat: ${response.code()} ${response.message()}"
                    _toastMessage.value = "Error: Failed to delete chat."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error deleting chat: ${e.localizedMessage}"
                _toastMessage.value = "Network error: Failed to delete chat."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toastMessageShown() {
        _toastMessage.value = null
    }
}