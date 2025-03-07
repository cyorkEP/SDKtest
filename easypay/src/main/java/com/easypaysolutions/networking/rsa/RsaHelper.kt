package com.easypaysolutions.networking.rsa

import com.easypaysolutions.exceptions.EasyPaySdkException

internal interface RsaHelper {
    @Throws(EasyPaySdkException::class)
    fun encrypt(data: String): String
}