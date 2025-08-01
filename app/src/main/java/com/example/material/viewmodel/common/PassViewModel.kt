package com.example.material.viewmodel.common

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.material.api.repo.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PassViewModel @Inject constructor(
    private val repo: UserRepository
) : ViewModel() {
    var isUpdating by mutableStateOf(false)
        private set

    fun updatePassword(
        password: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            isUpdating = true
            val result = repo.updatePassword(password)
            isUpdating = false

            result.fold(
                onSuccess = { onResult(true, it) },
                onFailure = { onResult(false, it.localizedMessage ?: "Unknown error") }
            )
        }
    }
}
