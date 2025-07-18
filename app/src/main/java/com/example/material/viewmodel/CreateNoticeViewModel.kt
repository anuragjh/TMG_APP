package com.example.material.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.material.api.NoticeRequest
import com.example.material.api.repo.TeacherClassRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface CreateNoticeState {
    object Idle     : CreateNoticeState
    object Sending  : CreateNoticeState
    object Success  : CreateNoticeState
    data class Error(val msg: String): CreateNoticeState
}

@HiltViewModel
class CreateNoticeViewModel @Inject constructor(
    private val repo: TeacherClassRepository
) : ViewModel() {

    private val _state = MutableStateFlow<CreateNoticeState>(CreateNoticeState.Idle)
    val state: StateFlow<CreateNoticeState> = _state.asStateFlow()

    fun send(topic: String, body: String, role: String, importance: String) =
        viewModelScope.launch {
            if (topic.isBlank() || body.isBlank()) {
                _state.value = CreateNoticeState.Error("Topic & body required")
                return@launch
            }

            _state.value = CreateNoticeState.Sending
            repo.createNotice(NoticeRequest(role, topic, body, importance))
                .onSuccess { _state.value = CreateNoticeState.Success }
                .onFailure { _state.value = CreateNoticeState.Error(it.message ?: "Unknown error") }
        }

    fun reset() { _state.value = CreateNoticeState.Idle }
}

