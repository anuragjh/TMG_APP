package com.example.material.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.material.api.ClassCreationRequest
import com.example.material.api.repo.ClassRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClassFormViewModel @Inject constructor(
    private val classRepository: ClassRepository
) : ViewModel() {

    // ✅ Store selected students and teachers
    private val _selectedStudents = MutableStateFlow<List<String>>(emptyList())
    val selectedStudents: StateFlow<List<String>> = _selectedStudents

    private val _selectedTeachers = MutableStateFlow<List<String>>(emptyList())
    val selectedTeachers: StateFlow<List<String>> = _selectedTeachers

    // ✅ Error/success message UI states
    var errorMessage by mutableStateOf("")
    var successMessage by mutableStateOf("")
    var isLoading by mutableStateOf(false)

    // ✅ Setters for selection
    fun setSelectedStudents(usernames: List<String>) {
        _selectedStudents.value = usernames
    }

    fun setSelectedTeachers(usernames: List<String>) {
        _selectedTeachers.value = usernames
    }

    fun clearSelections() {
        _selectedStudents.value = emptyList()
        _selectedTeachers.value = emptyList()
    }

    // ✅ Main API creation logic with validation
    fun createClass(name: String, feeStr: String) {
        errorMessage = ""
        successMessage = ""

        val trimmedName = name.trim()
        val fee = feeStr.toIntOrNull()

        when {
            trimmedName.isEmpty() -> errorMessage = "Class name is required"
            trimmedName.contains("/") || trimmedName.contains("\\") ->
                errorMessage = "Class name cannot contain '/' or '\\'"
            feeStr.isEmpty() -> errorMessage = "Fees are required"
            fee == null -> errorMessage = "Fee must be a number"
            fee > 10000 -> errorMessage = "Fee cannot exceed 10,000"
            _selectedStudents.value.isEmpty() -> errorMessage = "Select at least 1 student"
            _selectedTeachers.value.isEmpty() -> errorMessage = "Select at least 1 teacher"
            else -> {
                val request = ClassCreationRequest(
                    className = trimmedName,
                    fees = fee,
                    teachers = _selectedTeachers.value,
                    students = _selectedStudents.value
                )

                viewModelScope.launch {
                    isLoading = true
                    val result = classRepository.createClass(request)
                    isLoading = false
                    result.onSuccess {
                        successMessage = "Class created successfully"
                        clearSelections()
                    }.onFailure {
                        errorMessage = it.message ?: "Unknown error"
                    }
                }
            }
        }
    }
}
