package com.easypaysolutions.networking.authentication

import android.content.Context
import com.easypaysolutions.EasyPayConfiguration
import com.easypaysolutions.exceptions.EasyPaySdkException
import com.easypaysolutions.rules.koin.KoinNetworkingModulesRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

internal class TestAuthUtils : AuthUtils {
    companion object {
        const val TEST_EPOCH = "1702565789"
        const val TEST_DEVICE_ID = "123"
    }

    override fun getEpoch(): String = TEST_EPOCH
    override fun getDeviceId(context: Context): String = TEST_DEVICE_ID
}

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
internal class AuthHelperImplTest {

    @get:Rule
    val koinNetworkingModulesRule = KoinNetworkingModulesRule()

    companion object {
        private const val TEST_SESSION_KEY = "9B9175EF556E4DDA93303132323141303035383339"
        private const val TEST_HMAC_SECRET = "7D55DBB3D691C9E0FDF341E4AB38C3C9"
        private const val CORRECT_HASH =
            "1BD0C45A1DCBC7F9D0730FDD6222A45E10FCC7D9E84557499B812B138D0B7149"
    }

    @Mock
    private lateinit var context: Context
    private lateinit var authHelperImpl: AuthHelper

    @Before
    fun setUp() {
        setUpConfiguration()
    }

    @Test
    fun `getSessKey() returns 'sessKey' for requests without card data`() {
        val sessKey = authHelperImpl.getSessKey(false)
        assertEquals(TEST_SESSION_KEY, sessKey)
    }

    @Test
    fun `getSessKey() throws exception on empty SessKey`() {
        setUpConfiguration(sessionKey = "")
        EasyPayConfiguration.init("", TEST_HMAC_SECRET)
        try {
            authHelperImpl.getSessKey(true)
        } catch (e: EasyPaySdkException) {
            assertEquals(
                EasyPaySdkException.Type.MISSED_SESSION_KEY.message,
                e.message
            )
        }
    }

    @Test
    fun `getSessKey() throws exception on empty HMAC secret`() {
        setUpConfiguration(hmacSecret = "")
        EasyPayConfiguration.init(TEST_SESSION_KEY, "")
        try {
            authHelperImpl.getSessKey(true)
        } catch (e: EasyPaySdkException) {
            assertEquals(
                EasyPaySdkException.Type.MISSED_HMAC_SECRET.message,
                e.message
            )
        }
    }

    @Test
    fun `getHmacHash() returns correct HMAC hash`() {
        val hmacHash = authHelperImpl.getHmacHash(
            TEST_SESSION_KEY,
            TestAuthUtils.TEST_EPOCH,
            TestAuthUtils.TEST_DEVICE_ID,
            TEST_HMAC_SECRET
        )
        assertEquals(CORRECT_HASH, hmacHash)
    }

    @Test
    fun `getSessKey() returns correct hashed SessKey`() {
        val sessKey = authHelperImpl.getSessKey(true)
        val targetSessKey =
            TEST_SESSION_KEY + "_" + TestAuthUtils.TEST_EPOCH + "_" + TestAuthUtils.TEST_DEVICE_ID + "_" + CORRECT_HASH
        assertEquals(targetSessKey, sessKey)
    }

    //region Helpers

    private fun setUpConfiguration(
        sessionKey: String = TEST_SESSION_KEY,
        hmacSecret: String = TEST_HMAC_SECRET,
    ) {
        EasyPayConfiguration.init(sessionKey, hmacSecret)
        authHelperImpl = AuthHelperImpl(
            context,
            EasyPayConfiguration.getInstance(),
            TestAuthUtils()
        )
    }

    //endregion

}