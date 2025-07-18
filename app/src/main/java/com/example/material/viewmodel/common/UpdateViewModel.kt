package com.example.material.viewmodel.common

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.material.api.UpdateResponse
import com.example.material.api.repo.UpdateRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.buffer
import okio.sink
import java.io.File
import javax.inject.Inject

sealed class UpdateUiState {
    object Loading : UpdateUiState()
    data class Success(val response: UpdateResponse) : UpdateUiState()
    data class Error(val message: String) : UpdateUiState()
}

@HiltViewModel
class UpdateViewModel @Inject constructor(
    private val repository: UpdateRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow<UpdateUiState>(UpdateUiState.Loading)
    val uiState: StateFlow<UpdateUiState> = _uiState.asStateFlow()

    private val _downloadProgress = MutableStateFlow(0)
    val downloadProgress: StateFlow<Int> = _downloadProgress.asStateFlow()

    private val client = OkHttpClient()

    fun checkUpdate(currentVersion: String) {
        viewModelScope.launch {
            try {
                _uiState.value = UpdateUiState.Loading
                val response = repository.checkForUpdate(currentVersion)
                _uiState.value = UpdateUiState.Success(response)
            } catch (e: Exception) {
                _uiState.value = UpdateUiState.Error(e.localizedMessage ?: "Unknown error")
            }
        }
    }

    fun downloadAndInstallApk(apkUrl: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = Request.Builder().url(apkUrl).build()
                val response = client.newCall(request).execute()
                val body = response.body ?: return@launch

                val total = body.contentLength()
                var downloaded = 0L

                val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "update.apk")
                val sink = file.sink().buffer()

                body.source().use { source ->
                    var read: Long
                    val bufferSize = 8 * 1024L
                    while (source.read(sink.buffer, bufferSize).also { read = it } != -1L) {
                        downloaded += read
                        _downloadProgress.value = ((downloaded * 100) / total).toInt()
                    }
                    sink.close()
                }

                installApk(file)

            } catch (e: Exception) {
                Log.e("Download", "Failed: ${e.localizedMessage}")
            }
        }
    }

    private fun installApk(file: File) {
        val apkUri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
        }

        context.startActivity(intent)
    }
}
