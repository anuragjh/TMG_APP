package com.example.material.viewmodel.chat


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.material.api.ChatRoomResponse
import com.example.material.api.repo.ChatRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repo: ChatRepo
) : ViewModel() {

    private val _chatRooms = MutableStateFlow<List<ChatRoomResponse>>(emptyList())
    val chatRooms: StateFlow<List<ChatRoomResponse>> = _chatRooms

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    init {
        fetchChatRooms()
    }

    fun fetchChatRooms() {
        viewModelScope.launch {
            _loading.value = true
            try {
                _chatRooms.value = repo.getMyChatRooms()
            } catch (e: Exception) {
                _chatRooms.value = emptyList()
            }
            _loading.value = false
        }
    }
}
