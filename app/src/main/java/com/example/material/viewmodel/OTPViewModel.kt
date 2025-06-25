package com.example.material.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.material.api.ApiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OTPViewModel @Inject constructor(private val repository: ApiRepository.OTPRepository) : ViewModel() {

    sealed class OTPState {
        object Idle : OTPState()
        object Loading : OTPState()
        data class Success(val key: String) : OTPState()
        data class Error(val message: String) : OTPState()
    }

    private val _otpState = MutableStateFlow<OTPState>(OTPState.Idle)
    val otpState: StateFlow<OTPState> = _otpState

    fun verifyOtp(email: String, otp: String) {
        _otpState.value = OTPState.Loading
        viewModelScope.launch {
            val result = repository.verifyOtp(email, otp)
            _otpState.value = result.fold(
                onSuccess = { key -> OTPState.Success(key) },
                onFailure = { OTPState.Error("Invalid OTP") }
            )
        }
    }

    fun resetState() {
        _otpState.value = OTPState.Idle
    }
}



