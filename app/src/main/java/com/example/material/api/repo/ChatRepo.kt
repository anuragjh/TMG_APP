package com.example.material.api.repo

import android.content.Context
import com.example.material.api.ApiService
import com.example.material.api.ChatRoomResponse
import com.example.material.datastore.DataStoreManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepo @Inject constructor(
    private val service: ApiService,
    @ApplicationContext private val ctx: Context,
    private val dataStore: DataStoreManager
) {
    suspend fun getInitialMessages(className: String) = service.getMessages(className)

    suspend fun getToken(): String? = dataStore.getToken()

    suspend fun getRole(): String? = dataStore.getRole()

    suspend fun getUsername(): String? = dataStore.getUsername()

    suspend fun getMyChatRooms(): List<ChatRoomResponse> {
        return service.getMyChatRooms()
    }
}
