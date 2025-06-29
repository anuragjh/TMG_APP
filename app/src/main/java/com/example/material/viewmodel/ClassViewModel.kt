package com.example.material.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.material.api.ClassNameResponse
import com.example.material.api.repo.ClassRepositoryByName
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClassViewModel @Inject constructor(
    private val repository: ClassRepositoryByName
) : ViewModel() {

    private val _classes = MutableStateFlow<List<ClassNameResponse>>(emptyList())
    val classes: StateFlow<List<ClassNameResponse>> = _classes

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    fun fetchClassList() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val result = repository.getAllClasses()
                _classes.value = result
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _loading.value = false
            }
        }
    }
}
