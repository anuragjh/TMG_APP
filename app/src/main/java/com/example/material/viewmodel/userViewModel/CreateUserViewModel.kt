package com.example.material.viewmodel.userViewModel



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.material.api.CreateUserRequest
import com.example.material.api.repo.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateUserViewModel @Inject constructor(
    private val repo: UserRepository
) : ViewModel() {

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow("")
    val error: StateFlow<String> = _error

    private val _success = MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success

    fun createUser(request: CreateUserRequest) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = ""
            _success.value = false

            val result = repo.createUser(request)
            result
                .onSuccess {
                    _success.value = true
                }
                .onFailure {
                    _error.value = it.message ?: "Something went wrong"
                }

            _loading.value = false
        }
    }
}
