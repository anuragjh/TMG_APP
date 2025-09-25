package com.example.material.api.repo

import android.util.Log
import com.example.material.api.ApiService
import com.example.material.pages.admin.HealthMetrics
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HealthRepo @Inject constructor(
    private val healthApiService: ApiService
) {
    suspend fun getHealthMetrics(): Result<HealthMetrics> {
        return try {
            val metrics = healthApiService.getHealthMetrics()
            Result.success(metrics)
        } catch (e: Exception) {
            Log.e("HealthRepo", "Error fetching health metrics: ${e.localizedMessage}", e)
            Result.failure(e)
        }
    }
}