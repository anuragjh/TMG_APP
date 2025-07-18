package com.example.material.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor() : ViewModel() {
    private val _isLoggedOut = mutableStateOf(false)
    val isLoggedOut: State<Boolean> get() = _isLoggedOut

    fun logout() {
        _isLoggedOut.value = true
    }

    fun resetLogout() {
        _isLoggedOut.value = false
    }
}

