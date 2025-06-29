package com.example.material.viewmodel.userViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.material.api.User
import com.example.material.api.repo.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserUpdationViewModel @Inject constructor(
    private val repo: UserRepository
) : ViewModel() {

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    fun loadUsers() {
        viewModelScope.launch {
            _loading.value = true
            val result = repo.fetchAllUsers()
            result.onSuccess {
                _users.value = it
            }.onFailure {
                _users.value = emptyList()
            }
            _loading.value = false
        }
    }
}
