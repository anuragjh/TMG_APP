package com.example.material.viewmodel.student

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.material.api.DAYS
import com.example.material.api.RoutineEntry
import com.example.material.api.repo.StudentRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoutineViewModel @Inject constructor(
    private val repo: StudentRepo
) : ViewModel() {

    private val _routineMap = mutableStateOf<Map<DAYS, List<RoutineEntry>>>(emptyMap())
    val routineMap: State<Map<DAYS, List<RoutineEntry>>> = _routineMap

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    init {
        loadRoutine()
    }

    private fun loadRoutine() {
        viewModelScope.launch {
            _isLoading.value = true // Set loading to true before the API call
            try {
                val allRoutines = repo.fetchRoutine()
                val grouped = allRoutines.groupBy { it.dayOfWeek}
                _routineMap.value = grouped
            } catch (e: Exception) {
                _routineMap.value = emptyMap() // Handle error by clearing the map
                e.printStackTrace() // Log the error for debugging
            } finally {
                _isLoading.value = false // Set loading to false after the API call (success or failure)
            }
        }
    }
}