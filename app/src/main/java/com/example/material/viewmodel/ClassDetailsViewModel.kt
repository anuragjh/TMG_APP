package com.example.material.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.material.api.ClassDetails
import com.example.material.api.repo.ClassRepositoryForDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ClassDetailsState {
    object Loading : ClassDetailsState()
    data class Success(val data: ClassDetails) : ClassDetailsState()
    data class Error(val message: String) : ClassDetailsState()
}

@HiltViewModel
class ClassDetailsViewModel @Inject constructor(
    private val repository: ClassRepositoryForDetails
) : ViewModel() {

    private val _classDetailsState = MutableStateFlow<ClassDetailsState>(ClassDetailsState.Loading)
    val classDetailsState: StateFlow<ClassDetailsState> = _classDetailsState

    fun loadClassDetails(className: String) {
        viewModelScope.launch {
            _classDetailsState.value = ClassDetailsState.Loading
            try {
                val data = repository.getClassDetails(className)
                _classDetailsState.value = ClassDetailsState.Success(data)
            } catch (e: Exception) {
                _classDetailsState.value = ClassDetailsState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun deleteClass(
        className: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                val success = repository.deleteClass(className)
                if (success) {
                    onSuccess()
                } else {
                    onFailure()
                }
            } catch (e: Exception) {
                onFailure()
            }
        }
    }
}
