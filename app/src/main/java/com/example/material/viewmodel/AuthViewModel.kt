package com.example.material.viewmodel


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.material.DeviceTokenRequest
import com.example.material.TokenApi
import com.example.material.api.ApiRepository
import com.example.material.api.LoginResponse
import com.example.material.api.RetrofitClient
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val token: String, val role: String) : LoginState()
    data class Error(val message: String) : LoginState()
}

class AuthViewModel : ViewModel() {

    private val repository = ApiRepository(RetrofitClient.api)

    /* ✔ build TokenApi exactly once, no DI framework needed */
    private val tokenApi: TokenApi by lazy {
        RetrofitClient.retrofit.create(TokenApi::class.java)
    }



    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun login(username: String, password: String) = viewModelScope.launch {
        _loginState.value = LoginState.Loading
        repository.login(username, password)
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

    fun resetState() { _loginState.value = LoginState.Idle }

//    suspend fun getUsernameFromApi(): String? {
//        return try {
//            repository.getUsernameFromApi()
//        } catch (e: Exception) {
//            Log.e("AuthVM", "❌ Failed to fetch username", e)
//            null
//        }
//    }


    suspend fun uploadFcmToken(jwt: String): Boolean = coroutineScope {
        val token = runCatching { FirebaseMessaging.getInstance().token.await() }
            .onFailure { Log.e("AuthVM", "🔥 FCM token fetch failed", it) }
            .getOrNull()

        Log.d("AuthVM", "🔎  Token that will be POSTed: '$token'")

        if (token.isNullOrBlank()) return@coroutineScope false

        runCatching {
            tokenApi.sendToken("Bearer $jwt", DeviceTokenRequest(token))
            val json = Gson().toJson(DeviceTokenRequest(token))
            Log.d("DEBUG", "📦 Token JSON = $json")

        }
            .onFailure { Log.e("AuthVM", "❌ token upload failed", it) }
            .isSuccess
    }


}

