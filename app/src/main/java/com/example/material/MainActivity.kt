package com.example.material

import AppNavGraph
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
import android.Manifest
import android.os.Build
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.app.Activity

@Composable
fun RequestNotificationPermissionIfNeeded() {
    val context = LocalContext.current
    val activity = context as? Activity

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity!!,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                101
            )
        }
    }
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dataStoreManager = DataStoreManager(applicationContext)

//        enableEdgeToEdge()

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
                TMGTheme(darkTheme = false) {
                    RequestNotificationPermissionIfNeeded()
                    AppNavGraph(startDestination = startDestination)
                }
            }

        }
    }
}

