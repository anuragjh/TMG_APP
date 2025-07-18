package com.example.material.api.repo

import com.example.material.api.ApiService
import com.example.material.api.UpdateResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun checkForUpdate(version: String): UpdateResponse {
        return apiService.checkUpdate(version)
    }
}