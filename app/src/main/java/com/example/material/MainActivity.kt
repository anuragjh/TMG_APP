package com.example.material

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.lifecycle.lifecycleScope
import com.example.material.datastore.DataStoreManager
import com.example.material.ui.theme.TMGTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dataStoreManager = DataStoreManager(applicationContext)

        enableEdgeToEdge()

        lifecycleScope.launch {
            val token = dataStoreManager.getToken()
            val role = dataStoreManager.getRole()

            val startDestination = when {
                token != null && role != null -> {
                    when (role) {
                        "ADMIN" -> Screen.AdminHome.route
                        "TEACHER" -> Screen.TeacherHome.route
                        "STUDENT" -> Screen.StudentHome.route
                        else -> Screen.Login.route
                    }
                }
                else -> Screen.Login.route
            }

            setContent {
                TMGTheme(darkTheme = isSystemInDarkTheme()) {
                    AppNavGraph(startDestination = startDestination)
                }
            }
        }
    }
}

