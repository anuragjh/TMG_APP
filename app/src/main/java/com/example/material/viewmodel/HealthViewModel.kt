package com.example.material.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.material.api.repo.HealthRepo
import com.example.material.pages.admin.HealthMetrics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HealthUiState(
    val metrics: HealthMetrics? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class HealthViewModel @Inject constructor(
    private val healthRepo: HealthRepo
) : ViewModel() {

    private val _uiState = MutableStateFlow(HealthUiState())
    val uiState: StateFlow<HealthUiState> = _uiState.asStateFlow()

    init {
        fetchHealthMetrics()
    }

    fun fetchHealthMetrics() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val result = healthRepo.getHealthMetrics()
            result.onSuccess { metrics ->
                _uiState.value = _uiState.value.copy(metrics = metrics, isLoading = false)
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Server is Offline: ${e.localizedMessage}"
                )
            }
        }
    }
}