package com.easypaysolutions.networking.rsa

import android.content.Context
import com.easypaysolutions.exceptions.EasyPaySdkException
import com.easypaysolutions.utils.DownloadManager
import com.easypaysolutions.utils.SdkPreferences
import io.sentry.Sentry
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.PublicKey
import java.security.cert.X509Certificate
import java.util.Date

internal interface RsaCertificateManager {
    var certificate: X509Certificate?
    var certificateStatus: RsaCertificateStatus?

    @Throws(EasyPaySdkException::class)
    fun fetchCertificate()
    fun getPublicKey(): PublicKey?
}

internal class RsaCertificateManagerImpl(
    private val context: Context,
    private val downloadManager: DownloadManager,
    private val sdkPreferences: SdkPreferences,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : RsaCertificateManager {

    override var certificate: X509Certificate? = null
    override var certificateStatus: RsaCertificateStatus? = null

    companion object {
        private const val RSA_CERTIFICATE_FILE_NAME = "mobile.easypay5.com.cer"
        private const val RSA_CERTIFICATE_URL =
            "https://easypaysoftware.com/mobile.easypay5.com.cer"
        private const val REFRESH_TIME_LIMIT: Long = 7 * 24 * 60 * 60 * 1000L // 7 days
    }

    override fun fetchCertificate() {
        fetchCertificateFromLocalStorage()
        if (certificate == null || hasRefreshTimePassed()) {
            fetchCertificateFromUrl()
        }
    }

    override fun getPublicKey(): PublicKey? = certificate?.publicKey

    //region Private

    private fun fetchCertificateFromLocalStorage() {
        certificateStatus = RsaCertificateStatus.LOADING
        downloadManager.downloadFromLocalStorage(context, RSA_CERTIFICATE_FILE_NAME)?.let {
            try {
                parseBytes(it)
                certificateStatus = RsaCertificateStatus.SUCCESS
            } catch (e: EasyPaySdkException) {
                certificateStatus = RsaCertificateStatus.FAILED
                val exception = EasyPaySdkException(EasyPaySdkException.Type.RSA_CERTIFICATE_PARSING_ERROR)
                Sentry.captureException(exception)
                throw exception
            }
        }
    }

    private fun hasRefreshTimePassed(): Boolean {
        val now = Date().time
        val lastFetch = sdkPreferences.getLastRsaCertificateFetch()
        return now - lastFetch > REFRESH_TIME_LIMIT
    }

    private fun fetchCertificateFromUrl() {
        certificateStatus = RsaCertificateStatus.LOADING
        CoroutineScope(ioDispatcher).launch {
            downloadManager.downloadFrom(RSA_CERTIFICATE_URL) { bytes ->
                if (bytes == null) {
                    certificateStatus = RsaCertificateStatus.FAILED
                    throw EasyPaySdkException(EasyPaySdkException.Type.RSA_CERTIFICATE_FETCH_FAILED)
                }
                downloadManager.saveLocally(bytes, context, RSA_CERTIFICATE_FILE_NAME)
                try {
                    parseBytes(bytes)
                    certificateStatus = RsaCertificateStatus.SUCCESS
                    sdkPreferences.setLastRsaCertificateFetch(Date().time)
                } catch (e: EasyPaySdkException) {
                    certificateStatus = RsaCertificateStatus.FAILED
                    val exception = EasyPaySdkException(EasyPaySdkException.Type.RSA_CERTIFICATE_PARSING_ERROR)
                    Sentry.captureException(exception)
                    throw exception
                }
            }
        }
    }

    private fun parseBytes(bytes: ByteArray) {
        certificate = RsaUtils.convertToCertificate(bytes)
    }

    //endregion

}