package com.easypaysolutions.networking.rsa

import android.util.Base64
import com.easypaysolutions.exceptions.EasyPaySdkException
import io.sentry.Sentry
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.security.PrivateKey
import java.security.PublicKey
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.crypto.Cipher

internal object RsaUtils {

    private const val TRANSFORMATION = "RSA/ECB/OAEPWithSHA-1AndMGF1Padding"
    private const val CERTIFICATE_TYPE = "X.509"

    fun convertToCertificate(bytes: ByteArray): X509Certificate? {
        return try {
            val certificateFactory = CertificateFactory.getInstance(CERTIFICATE_TYPE)
            val certificateInputStream: InputStream = ByteArrayInputStream(bytes)
            certificateFactory.generateCertificate(certificateInputStream) as? X509Certificate
        } catch (e: Exception) {
            val exception =
                EasyPaySdkException(EasyPaySdkException.Type.RSA_CERTIFICATE_PARSING_ERROR)
            Sentry.captureException(exception)
            throw exception
        }
    }

    fun encrypt(
        data: String,
        publicKey: PublicKey,
        transformation: String = TRANSFORMATION,
    ): String {
        val cipher = Cipher.getInstance(transformation)
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        val bytes = cipher.doFinal(data.toByteArray())
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    fun decrypt(
        data: String,
        privateKey: PrivateKey,
        transformation: String = TRANSFORMATION,
    ): String {
        val cipher = Cipher.getInstance(transformation)
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        val decode = Base64.decode(data, Base64.DEFAULT)
        return String(cipher.doFinal(decode))
    }
}