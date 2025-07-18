package com.example.material

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.material.datastore.DataStoreManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.annotations.SerializedName
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import javax.inject.Inject


data class DeviceTokenRequest(   @SerializedName("token")val token: String)

/* ── Retrofit interface ──────────────────────────────────────────── */
interface TokenApi {
    @POST("/api/device/token")
    suspend fun sendToken(
        @Header("Authorization") bearer: String,
        @Body request: DeviceTokenRequest
    )
}

/* ── Firebase Messaging Service ──────────────────────────────────── */
@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    @Inject lateinit var tokenApi : TokenApi
    @Inject lateinit var dataStore: DataStoreManager  // stores JWT locally

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Refreshed token = $token")

        CoroutineScope(Dispatchers.IO).launch {
            val jwt = dataStore.getToken()          // e.g. "eyJhbGciOiJIUzI1NiIs..."
            if (jwt.isNullOrBlank()) {
                Log.w("FCM", "No JWT in DataStore – skip token upload")
                return@launch
            }
            uploadToken(jwt, token)
        }
    }

    private suspend fun uploadToken(jwt: String, fcmToken: String) {
        val bearer = "Bearer $jwt"
        try {
            tokenApi.sendToken(bearer, DeviceTokenRequest(fcmToken))
            Log.d("FCM", "✅ token sent to backend")
        } catch (e: Exception) {
            Log.e("FCM", "❌ failed to send token", e)
        }
    }

    /* Handle incoming push when app is in foreground */
    override fun onMessageReceived(message: RemoteMessage) {
        message.notification?.let { showNotification(it.title, it.body) }
    }

    /* Simple local notification */
    private fun showNotification(title: String?, body: String?) {
        val channelId = "tmg_default"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ch = NotificationChannel(
                channelId, "General",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(ch)
        }

        val notif = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.logo)   // 🔁 use your own icon
            .setContentTitle(title ?: "TMG")
            .setContentText(body ?: "")
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(this)
            .notify(System.currentTimeMillis().toInt(), notif)
    }
}
