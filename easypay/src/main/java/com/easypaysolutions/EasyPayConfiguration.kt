package com.easypaysolutions

import com.easypaysolutions.exceptions.EasyPaySdkException
import com.easypaysolutions.networking.rsa.RsaCertificateManager
import com.easypaysolutions.networking.rsa.RsaCertificateStatus
import com.easypaysolutions.utils.VersionManager
import org.koin.java.KoinJavaComponent

data class EasyPayConfiguration internal constructor(
    private val sessionKey: String,
    private val hmacSecret: String,
    private val versionManager: VersionManager = VersionManager(),
) {

    private val rsaCertificateManager: RsaCertificateManager by KoinJavaComponent.inject(
        RsaCertificateManager::class.java
    )
    private val sdkVersion: String = versionManager.getCurrentSdkVersion()

    companion object {

        internal var API_VERSION = BuildConfig.API_VERSION

        private var instance: EasyPayConfiguration? = null

        @Throws(EasyPaySdkException::class)
        fun getInstance(): EasyPayConfiguration {
            return instance ?: throw EasyPaySdkException(
                EasyPaySdkException.Type.EASY_PAY_CONFIGURATION_NOT_INITIALIZED
            )
        }

        internal fun init(
            sessionKey: String,
            hmacSecret: String,
        ) {
            instance = EasyPayConfiguration(
                sessionKey = sessionKey,
                hmacSecret = hmacSecret
            )
            instance?.rsaCertificateManager?.fetchCertificate()
        }

        internal fun reset() {
            instance = null
        }
    }

    fun getSdkVersion(): String = sdkVersion

    fun getSessionKey(): String = sessionKey

    fun getHmacSecret(): String = hmacSecret

    fun getRsaCertificateFetchingStatus(): RsaCertificateStatus? =
        rsaCertificateManager.certificateStatus
}