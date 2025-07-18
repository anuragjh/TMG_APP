package com.example.material.viewmodel.common


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.material.api.repo.TeacherClassRepository
import com.example.material.pages.commons.Notice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface NoticeUiState {
    object Loading : NoticeUiState
    data class Success(val list: List<Notice>) : NoticeUiState
    data class Error(val msg: String) : NoticeUiState
}

@HiltViewModel
class NoticeViewModel @Inject constructor(
    private val repo: TeacherClassRepository
) : ViewModel() {

    private val _state = MutableStateFlow<NoticeUiState>(NoticeUiState.Loading)
    val state: StateFlow<NoticeUiState> = _state.asStateFlow()

    init { load() }

    init { load() }

    fun load() = viewModelScope.launch {
        Log.d("NoticeFlow", "VM load()")
        _state.value = NoticeUiState.Loading
        repo.fetch()
            .onSuccess {
                Log.d("NoticeFlow", "VM success – list size=${it.size}")
                _state.value = NoticeUiState.Success(it)
            }
            .onFailure {
                Log.e("NoticeFlow", "VM error", it)
                _state.value = NoticeUiState.Error(it.message ?: "Unknown error")
            }
    }


    fun refresh() = load()
}
