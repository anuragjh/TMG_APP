package com.example.material.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.material.api.ApiRepository
import com.example.material.api.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(
    private val repo: ApiRepository = ApiRepository(RetrofitClient.api)
) : ViewModel() {

    private val _forgotState = MutableStateFlow<ForgotPasswordState>(ForgotPasswordState.Idle)
    val forgotState: StateFlow<ForgotPasswordState> = _forgotState

    fun sendOtp(email: String) {
        viewModelScope.launch {
            _forgotState.value = ForgotPasswordState.Loading
            val result = repo.forgotPassword(email)
            _forgotState.value = result.fold(
                onSuccess = { ForgotPasswordState.Success(it) },
                onFailure = { ForgotPasswordState.Error(it.message ?: "Unknown error") }
            )
        }
    }


    fun reset() {
        _forgotState.value = ForgotPasswordState.Idle
    }
}

sealed class ForgotPasswordState {
    object Idle : ForgotPasswordState()
    object Loading : ForgotPasswordState()
    data class Success(val msg: String) : ForgotPasswordState()
    data class Error(val msg: String) : ForgotPasswordState()
}