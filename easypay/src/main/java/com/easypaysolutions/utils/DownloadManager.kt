package com.easypaysolutions.utils

import android.content.Context
import io.sentry.Sentry
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.URL

internal interface DownloadManager {
    fun downloadFrom(
        urlString: String,
        byteArraySize: Int = 4096,
        callback: (ByteArray?) -> Unit,
    )

    fun downloadFromLocalStorage(context: Context, fileName: String): ByteArray?

    fun saveLocally(byteArray: ByteArray, context: Context, fileName: String)
}

internal class DownloadManagerImpl(
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : DownloadManager {
    override fun downloadFrom(
        urlString: String,
        byteArraySize: Int,
        callback: (ByteArray?) -> Unit,
    ) {
        CoroutineScope(ioDispatcher).launch {
            val url = URL(urlString)
            val output = ByteArrayOutputStream()
            withContext(ioDispatcher) {
                url.openStream()
            }.use { stream ->
                val buffer = ByteArray(byteArraySize)
                while (true) {
                    val bytesRead = stream.read(buffer)
                    if (bytesRead < 0) {
                        break
                    }
                    output.write(buffer, 0, bytesRead)
                }
            }
            callback(output.toByteArray())
        }
    }

    override fun downloadFromLocalStorage(context: Context, fileName: String): ByteArray? {
        return try {
            val file = File(context.filesDir, fileName)
            file.inputStream().readBytes()
        } catch (e: Exception) {
            // file not yet created
            Sentry.captureException(e)
            null
        }
    }

    override fun saveLocally(byteArray: ByteArray, context: Context, fileName: String) {
        val file = File(context.filesDir, fileName)
        file.writeBytes(byteArray)
    }
}