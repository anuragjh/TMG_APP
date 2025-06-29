package com.example.material.viewmodel

import com.example.material.api.ApiRepository

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.material.pages.admin.CLASS.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: ApiRepository.UserRepository
) : ViewModel() {

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    fun loadUsers(role: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                _users.value = userRepository.getUsers(role)
            } catch (e: Exception) {
                _users.value = emptyList()
            } finally {
                _loading.value = false
            }
        }
    }
}
