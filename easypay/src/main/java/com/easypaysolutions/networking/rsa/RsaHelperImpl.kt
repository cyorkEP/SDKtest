package com.easypaysolutions.networking.rsa

import com.easypaysolutions.exceptions.EasyPaySdkException
import java.security.MessageDigest
import java.security.cert.X509Certificate

internal class RsaHelperImpl(
    private val rsaCertificateManager: RsaCertificateManager,
) : RsaHelper {

    //region Overridden

    override fun encrypt(data: String): String {
        if (data.isEmpty()) {
            return data
        }
        val publicKey = rsaCertificateManager.getPublicKey()
        publicKey?.let {
            val encrypted = RsaUtils.encrypt(data, it)
            return applyFingerprint(encrypted)
        }
        throw EasyPaySdkException(EasyPaySdkException.Type.RSA_CERTIFICATE_NOT_FETCHED)
    }

    //endregion

    //region Private

    private fun applyFingerprint(encrypted: String): String {
         return "$encrypted|${getFingerprint()}"
    }

    private fun getFingerprint(): String {
        val cert = rsaCertificateManager.certificate ?: return ""
        return getDigest(cert)
    }

    private fun getDigest(cert: X509Certificate): String {
        val digest = MessageDigest.getInstance("SHA1")
        val bytes = digest.digest(cert.encoded)
        val hexString = StringBuilder()
        for (b in bytes) {
            val hex = String.format("%02x", b)
            hexString.append(hex)
        }
        return hexString.toString()
    }

    //endregion

}

