package com.example.material.utils

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File


@Composable
fun DownloadAndOpenNote(
    remoteUrl: String,
    displayName: String,
    onProgress : (Int) -> Unit,    // 0‒100
    onFinished: () -> Unit
) {
    val ctx = LocalContext.current

    LaunchedEffect(remoteUrl) {
        try {
            /* -------- download (with progress) -------- */
            val cached = withContext(Dispatchers.IO) {
                val ext  = remoteUrl.substringAfterLast('.', "")
                val file = File(ctx.cacheDir, "notes/$displayName.$ext").apply {
                    parentFile?.mkdirs()
                }

                if (!file.exists()) {
                    val resp = OkHttpClient()
                        .newCall(Request.Builder().url(remoteUrl).build())
                        .execute()

                    val total = resp.body?.contentLength() ?: -1L
                    var read  = 0L

                    resp.body?.byteStream()?.use { input ->
                        file.outputStream().use { out ->
                            val buf = ByteArray(8 * 1024)
                            var n: Int
                            while (input.read(buf).also { n = it } != -1) {
                                out.write(buf, 0, n)
                                read += n
                                if (total > 0) {
                                    val pct = (read * 100 / total).toInt()
                                    onProgress(pct)
                                }
                            }
                        }
                    }
                }
                file
            }

            /* -------- open via FileProvider -------- */
            val uri: Uri = FileProvider.getUriForFile(
                ctx, "${ctx.packageName}.fileprovider", cached
            )
            val mime = ctx.contentResolver.getType(uri) ?: "*/*"
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, mime)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            ctx.startActivity(Intent.createChooser(intent, "Open with"))
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            onProgress(0)      // reset
            onFinished()
        }
    }
}
