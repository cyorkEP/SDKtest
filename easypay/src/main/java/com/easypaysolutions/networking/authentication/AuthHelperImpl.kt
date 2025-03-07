package com.easypaysolutions.networking.authentication

import android.content.Context
import com.easypaysolutions.EasyPayConfiguration
import com.easypaysolutions.exceptions.EasyPaySdkException
import com.easypaysolutions.utils.toHex
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

internal class AuthHelperImpl(
    private val context: Context,
    private val configuration: EasyPayConfiguration = EasyPayConfiguration.getInstance(),
    private val authUtils: AuthUtils,
) : AuthHelper {

    companion object {
        const val ALGORITHM_NAME = "HmacSHA256"
    }

    override fun getSessKey(userDataPresent: Boolean): String {
        val sessionKey = configuration.getSessionKey()
        if (sessionKey.isBlank()) {
            throw EasyPaySdkException(EasyPaySdkException.Type.MISSED_SESSION_KEY)
        }
        if (!userDataPresent) {
            return sessionKey
        }
        val hmacSecret = configuration.getHmacSecret()
        if (hmacSecret.isBlank()) {
            throw EasyPaySdkException(EasyPaySdkException.Type.MISSED_HMAC_SECRET)
        }

        val deviceId = authUtils.getDeviceId(context)
        val epoch = authUtils.getEpoch()

        val hash = getHmacHash(sessionKey, epoch, deviceId, hmacSecret)
        return sessionKey + "_" + epoch + "_" + deviceId + "_" + hash
    }

    override fun getHmacHash(
        sessionKey: String,
        epoch: String,
        deviceId: String,
        hmacSecret: String,
    ): String {
        val hmac = Mac.getInstance(ALGORITHM_NAME)
        val secretKey = SecretKeySpec(hmacSecret.encodeToByteArray(), ALGORITHM_NAME)
        hmac.init(secretKey)
        val hashable = sessionKey + "_" + epoch + "_" + deviceId
        return hmac.doFinal(hashable.encodeToByteArray()).toHex()
    }
}