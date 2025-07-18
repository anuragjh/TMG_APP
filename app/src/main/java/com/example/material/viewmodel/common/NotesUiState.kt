package com.example.material.viewmodel.common


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.material.api.repo.TeacherClassRepository
import com.example.material.pages.teacher.NoteItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class NotesUiState {
    object Loading                       : NotesUiState()
    data class Success(
        val grouped: Map<String, List<NoteItem>>
    )                                    : NotesUiState()
    data class Error(val msg: String)    : NotesUiState()
}

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val repo: TeacherClassRepository
) : ViewModel() {

    private val _state      = MutableStateFlow<NotesUiState>(NotesUiState.Loading)
    val state: StateFlow<NotesUiState> = _state.asStateFlow()

    /** Exposed to the UI for showing a global “deleting…” spinner */
    private val _isDeleting = MutableStateFlow(false)
    val isDeleting: StateFlow<Boolean> = _isDeleting.asStateFlow()

    init { loadNotes() }

    fun loadNotes() = viewModelScope.launch {
        _state.value = NotesUiState.Loading
        runCatching { repo.fetchNotes() }
            .onSuccess { res ->
                val body = res.body()
                if (res.isSuccessful && body != null) {
                    val grouped = body
                        .groupBy { it.className ?: "General" }
                        .toSortedMap()
                    _state.value = NotesUiState.Success(grouped)
                } else {
                    _state.value = NotesUiState.Error(
                        res.errorBody()?.string() ?: "Unknown error"
                    )
                }
            }
            .onFailure { e ->
                _state.value = NotesUiState.Error(e.localizedMessage ?: "Network error")
            }
    }

    /** Delete selected notes, then refresh list */
    fun deleteNotes(names: List<String>) = viewModelScope.launch {
        if (names.isEmpty()) return@launch
        _isDeleting.value = true
        runCatching { repo.deleteNotes(names) }
            .onSuccess {
                // You can inspect response if needed; here we simply reload list
                loadNotes()
            }
            .onFailure { e ->
                _state.value = NotesUiState.Error(e.localizedMessage ?: "Delete failed")
            }
        _isDeleting.value = false
    }
}
