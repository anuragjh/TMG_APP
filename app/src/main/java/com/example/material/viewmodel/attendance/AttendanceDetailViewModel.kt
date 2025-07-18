package com.example.material.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.material.api.AttendanceDetail
import com.example.material.api.repo.AttendanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttendanceDetailViewModel @Inject constructor(
    private val repository: AttendanceRepository
) : ViewModel() {

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _detail = MutableStateFlow<AttendanceDetail?>(null)
    val detail: StateFlow<AttendanceDetail?> = _detail.asStateFlow()

    fun load(attendanceId: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val data = repository.fetchDetail(attendanceId)
                _detail.value = data
            } catch (e: Exception) {
                e.printStackTrace()
                _detail.value = null
            } finally {
                _loading.value = false
            }
        }
    }
}
