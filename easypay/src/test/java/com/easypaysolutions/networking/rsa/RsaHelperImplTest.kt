package com.easypaysolutions.networking.rsa

import com.easypaysolutions.exceptions.EasyPaySdkException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.cert.X509Certificate

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
internal class RsaHelperImplTest {

    private lateinit var rsaCertificateManager: TestRsaCertificateManager
    private lateinit var rsaHelper: RsaHelper

    companion object {
        private const val TEST_CREDIT_CARD = "4761530001111118"
    }

    @Before
    fun setUp() {
        rsaCertificateManager = TestRsaCertificateManager()
        rsaHelper = RsaHelperImpl(rsaCertificateManager)
    }

    @Test
    fun `encrypt() returns correct encrypted data`() {
        rsaCertificateManager.fetchCertificate()
        val encryptedText = rsaHelper.encrypt(TEST_CREDIT_CARD)
        val decryptedText = RsaUtils.decrypt(encryptedText, rsaCertificateManager.privateKey)
        assertEquals(TEST_CREDIT_CARD, decryptedText)
    }

    @Test
    fun `not calling fetchCertificate() before encrypt() throws EasyPaySdkException`() {
        try {
            rsaHelper.encrypt(TEST_CREDIT_CARD)
        } catch (e: EasyPaySdkException) {
            assertEquals(
                EasyPaySdkException.Type.RSA_CERTIFICATE_NOT_FETCHED.message,
                e.message
            )
        }
    }
}

internal class TestRsaCertificateManager : RsaCertificateManager {

    companion object {
        private const val ALGORITHM = "RSA"
        private const val KEY_SIZE = 2048
    }

    override var certificate: X509Certificate? = null
    override var certificateStatus: RsaCertificateStatus? = null

    lateinit var privateKey: PrivateKey
    private var publicKey: PublicKey? = null

    override fun fetchCertificate() {
        val keyGen = KeyPairGenerator.getInstance(ALGORITHM)
        keyGen.initialize(KEY_SIZE)
        val pair = keyGen.generateKeyPair()
        publicKey = pair.public
        privateKey = pair.private
    }

    override fun getPublicKey(): PublicKey? = publicKey
}