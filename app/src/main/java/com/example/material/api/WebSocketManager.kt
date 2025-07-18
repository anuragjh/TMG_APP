package com.example.material.api

import android.os.Handler
import android.os.Looper
import android.util.Log
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.json.JSONObject
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent

object WebSocketManager {
    private var stompClient: StompClient? = null
    private val compositeDisposable = CompositeDisposable()
    private var reconnectAttempts = 0
    private var connected = false
    private var reconnecting = false
    private val reconnectHandler = Handler(Looper.getMainLooper())

    fun connect(token: String, className: String, onMessageReceived: (JSONObject) -> Unit) {
        if (connected || reconnecting) return

        reconnecting = reconnectAttempts > 0
        val url = "ws://140.245.28.59:8080/ws/websocket?token=$token"

        try {
            stompClient?.disconnect()
            stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, url).apply {
                // Lifecycle handling
                compositeDisposable.add(lifecycle()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ lifecycleEvent ->
                        when (lifecycleEvent.type) {
                            LifecycleEvent.Type.OPENED -> {
                                Log.d("STOMP", "✅ Connected")
                                connected = true
                                reconnecting = false
                                reconnectAttempts = 0
                            }
                            LifecycleEvent.Type.ERROR -> {
                                Log.e("STOMP", "❌ Connection error", lifecycleEvent.exception)
                                handleDisconnection(token, className, onMessageReceived)
                            }
                            LifecycleEvent.Type.CLOSED -> {
                                Log.d("STOMP", "🔒 Connection closed")
                                handleDisconnection(token, className, onMessageReceived)
                            }
                            LifecycleEvent.Type.FAILED_SERVER_HEARTBEAT -> {
                                Log.w("STOMP", "💔 Server heartbeat failed")
                            }
                            else -> {
                                Log.d("STOMP", "ℹ️ Lifecycle event: ${lifecycleEvent.type}")
                            }
                        }
                    }, { error ->
                        Log.e("STOMP", "❗ Lifecycle error", error)
                        handleDisconnection(token, className, onMessageReceived)
                    }))

                // Message subscription with robust parsing
                compositeDisposable.add(topic("/topic/class/$className")
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ message ->
                        try {
                            Log.d("Raw STOMP Frame", "»» Full Message: ${message.payload}")
                            Log.d("Raw STOMP Frame", "»» As Bytes: ${String(message.payload.toByteArray())}")

                            val payload = cleanPayload(message.payload)
                            Log.d("STOMP", "📥 Raw message: $payload")

                            val json = JSONObject(payload)
                            if (validateMessage(json)) {
                                onMessageReceived(json)
                            } else {
                                Log.w("STOMP", "⚠️ Invalid message format: $json")
                            }
                        } catch (e: Exception) {
                            Log.e("STOMP", "❌ Failed to parse message: ${message.payload}", e)
                        }
                    }, { error ->
                        Log.e("STOMP", "❗ Subscription error", error)
                    }))


                // Connect with timeout
                connect()
                Log.d("STOMP", "🔗 Connecting to $url...")
            }
        } catch (e: Exception) {
            Log.e("STOMP", "❗ Initial connection error", e)
            handleDisconnection(token, className, onMessageReceived)
        }
    }

    private fun cleanPayload(payload: String): String {
        return payload
            .trim()
            .replace(Regex("\\?+$"), "") // Remove trailing question marks
            .replace(Regex("[^\\x20-\\x7E]"), "") // Remove non-ASCII chars
            .replace(Regex("([}\\]])[^}\\]]*$"), "$1") // Truncate after last } or ]
    }

    private fun validateMessage(json: JSONObject): Boolean {
        return json.has("id") &&
                json.has("className") &&
                json.has("senderUsername") &&
                json.has("message") &&
                json.has("timestamp")
    }

    private fun handleDisconnection(
        token: String,
        className: String,
        onMessageReceived: (JSONObject) -> Unit
    ) {
        connected = false
        if (reconnectAttempts < 5) {
            val delay = 3000L * (reconnectAttempts + 1)
            reconnectAttempts++
            Log.d("STOMP", "🔁 Scheduling reconnect in ${delay}ms (attempt $reconnectAttempts/5)")

            reconnectHandler.postDelayed({
                Log.d("STOMP", "🔗 Attempting reconnect...")
                connect(token, className, onMessageReceived)
            }, delay)
        } else {
            Log.e("STOMP", "⚠️ Maximum reconnect attempts reached")
            reconnecting = false
        }
    }

    fun send(destination: String, body: JSONObject) {
        if (!connected) {
            Log.w("STOMP", "⚠️ Not connected - message not sent")
            return
        }

        try {
            stompClient?.send(destination, body.toString())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({
                    Log.d("STOMP", "📤 Sent to $destination")
                }, { error ->
                    Log.e("STOMP", "❌ Send error", error)
                })?.let { compositeDisposable.add(it) }
        } catch (e: Exception) {
            Log.e("STOMP", "❗ Failed to send message", e)
        }
    }

    fun disconnect() {
        reconnectHandler.removeCallbacksAndMessages(null)
        compositeDisposable.clear()
        stompClient?.disconnect()
        stompClient = null
        connected = false
        reconnectAttempts = 0
        reconnecting = false
        Log.d("STOMP", "🔌 Disconnected cleanly")
    }
}