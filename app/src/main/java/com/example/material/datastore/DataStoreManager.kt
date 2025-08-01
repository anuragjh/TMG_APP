package com.example.material.datastore

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

object DataStoreKeys {
    val TOKEN = stringPreferencesKey("token")
    val ROLE = stringPreferencesKey("role")
}

class DataStoreManager(private val context: Context) {

    suspend fun saveAuth(token: String, role: String) {
        context.dataStore.edit { prefs ->
            prefs[DataStoreKeys.TOKEN] = token
            prefs[DataStoreKeys.ROLE] = role
        }
    }

    //added for username management
    suspend fun saveUsername(username: String) {
        context.dataStore.edit { prefs ->
            prefs[stringPreferencesKey("username")] = username
        }
    }

    suspend fun clearAuth() {
        context.dataStore.edit {
            it.remove(DataStoreKeys.TOKEN)
            it.remove(DataStoreKeys.ROLE)
            it.remove(stringPreferencesKey("username")) // Clear username as well
        }
    }

    suspend fun getToken(): String? {
        return context.dataStore.data.first()[DataStoreKeys.TOKEN]
    }

    suspend fun getRole(): String? {
        return context.dataStore.data.first()[DataStoreKeys.ROLE]
    }
    suspend fun getUsername(): String? {
        return context.dataStore.data.first()[stringPreferencesKey("username")]
    }
}
