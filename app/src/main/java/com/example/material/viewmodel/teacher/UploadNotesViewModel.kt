package com.example.material.viewmodel.teacher

import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.material.api.ClassResponse
import com.example.material.api.repo.TeacherClassRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/* ---------- UI state -------------------------------------------- */
sealed interface UploadNotesUiState {
    object Loading : UploadNotesUiState
    data class Error(val msg: String) : UploadNotesUiState
    data class Success(val classes: List<ClassResponse>) : UploadNotesUiState
}

sealed interface UploadStatus {
    object Idle : UploadStatus
    object Uploading : UploadStatus
    data class Done(val ok: Boolean, val message: String) : UploadStatus
}

/* ---------- ViewModel ------------------------------------------- */
@HiltViewModel
class UploadNotesViewModel @Inject constructor(
    private val repo: TeacherClassRepository
) : ViewModel() {

    private val _uiState       = mutableStateOf<UploadNotesUiState>(UploadNotesUiState.Loading)
    private val _uploadStatus  = mutableStateOf<UploadStatus>(UploadStatus.Idle)

    val uiState: State<UploadNotesUiState>  = _uiState
    val uploadStatus: State<UploadStatus>   = _uploadStatus

    /* list of classes */
    fun refresh() = viewModelScope.launch {
        _uiState.value = UploadNotesUiState.Loading
        runCatching { repo.getAllClasses() }
            .onSuccess { _uiState.value = UploadNotesUiState.Success(it) }
            .onFailure { _uiState.value = UploadNotesUiState.Error(it.message ?: "Unknown error") }
    }

    /* upload a single note */
    fun upload(classInfo: ClassResponse, uri: Uri, displayName: String) =
        viewModelScope.launch {
            _uploadStatus.value = UploadStatus.Uploading
            runCatching {
                repo.uploadNote(classInfo, uri, displayName)
            }.onSuccess {
                _uploadStatus.value = UploadStatus.Done(true, "Uploaded ✔")
            }.onFailure {
                _uploadStatus.value = UploadStatus.Done(false, it.message ?: "Upload failed")
            }
        }

    fun resetStatus() { _uploadStatus.value = UploadStatus.Idle }
}
