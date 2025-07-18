package com.example.material.api.repo


import com.example.material.api.ApiService
import com.example.material.api.AttendanceDetail
import javax.inject.Inject

class AttendanceRepository @Inject constructor(
    private val service: ApiService
) {
    suspend fun fetchSummary() = service.getAttendanceSummary()
    suspend fun fetchDetail(attendanceId: String): AttendanceDetail {
        return service.getAttendanceDetail(attendanceId)
    }
}