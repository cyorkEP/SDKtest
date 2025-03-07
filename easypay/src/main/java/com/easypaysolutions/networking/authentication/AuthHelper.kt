package com.easypaysolutions.networking.authentication

import com.easypaysolutions.exceptions.EasyPaySdkException
import java.security.InvalidKeyException

internal interface AuthHelper {
    @Throws(InvalidKeyException::class, EasyPaySdkException::class)
    fun getSessKey(userDataPresent: Boolean): String

    @Throws(InvalidKeyException::class)
    fun getHmacHash(
        sessionKey: String,
        epoch: String,
        deviceId: String,
        hmacSecret: String,
    ): String
}