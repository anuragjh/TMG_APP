package com.example.material.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.material.api.ApiRepository
import com.example.material.api.LoginResponse
import com.example.material.api.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val token: String, val role: String) : LoginState()
    data class Error(val message: String) : LoginState()
}

class AuthViewModel : ViewModel() {

    private val repository = ApiRepository(RetrofitClient.api)

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun login(username: String, password: String) {
        _loginState.value = LoginState.Loading
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            val result = repository.login(username, password)
            result
                .onSuccess {
                    if (it.jwt != null && it.role != null) {
                        _loginState.value = LoginState.Success(it.jwt, it.role)
                    } else {
                        _loginState.value = LoginState.Error("Missing token or role from server")
                    }
                }
                .onFailure {
                    _loginState.value = LoginState.Error(it.localizedMessage ?: "Login failed")
                }
        }

    }

    fun resetState() {
        _loginState.value = LoginState.Idle
    }
}
