package com.example.material.viewmodel.chat

import android.app.Application
import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.material.R
import com.example.material.api.WebSocketManager
import com.example.material.api.repo.ChatRepo
import com.example.material.pages.commons.ChatMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class ChatRoomViewModel @Inject constructor(
    application: Application,
    private val repo: ChatRepo
) : AndroidViewModel(application) {




    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val _role = MutableStateFlow<String?>(null)
    val role: StateFlow<String?> = _role

    private var hasInitialized = false


    fun init(className: String, canEveryoneMessage: Boolean, username: String) {
        if (hasInitialized) return
        hasInitialized = true

        viewModelScope.launch {
            _messages.value = repo.getInitialMessages(className)
            Log.d("ChatVM", "📥 Initial messages loaded: ${_messages.value} ")
            val currentRole = repo.getRole()
            _role.value = currentRole

            val token = repo.getToken()

            if (token != null) {
                WebSocketManager.connect(token, className) { json ->
                    Log.d("WebSocketRaw", "Received: $json")
                    val msg = ChatMessage(
                        id = json.optString("id", ""),
                        senderUsername = json.optString("senderUsername", ""),
                        message = json.optString("message", ""),
                        timestamp = json.optString("timestamp", ""),
                        className = json.optString("className", "")
                    )

                    if (msg.id.isBlank() || msg.message.isBlank()) {
                        Log.w("ChatVM", "🧨 Ignoring malformed message: $json")
                        return@connect
                    }

                    Log.d("ChatVM", "👁 Parsed message: $msg")

                    _messages.update { old -> old.toList() + msg }


                    // Only play receive sound for messages from others
                    if (msg.senderUsername != username) {
                        playReceiveSound()
                    }
                }
            }
        }
    }

    private fun playReceiveSound() {
            try {
                val mediaPlayer = MediaPlayer.create(getApplication(), R.raw.received)
                mediaPlayer.setOnCompletionListener { it.release() }
                mediaPlayer.start()
            } catch (e: Exception) {
                Log.e("ChatVM", "🎵 Receive sound failed: ${e.message}")
            }
        }


    fun sendMessage(className: String, msg: String) {
        viewModelScope.launch {
            val username = repo.getUsername()
            val messageBody = JSONObject().apply {
                put("className", className)
                put("message", msg)
                put("senderUsername", username) // ✅ this is REQUIRED by backend
            }

            WebSocketManager.send("/app/chat/$className", messageBody)
        }
    }

    override fun onCleared() {
        super.onCleared()
        WebSocketManager.disconnect()
    }
}
