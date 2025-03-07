package com.easypaysolutions.utils

import com.easypaysolutions.rules.MainDispatcherRule
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
internal class DownloadManagerTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val downloadManager: DownloadManager =
        DownloadManagerImpl(mainDispatcherRule.testDispatcher)

    companion object {
        private const val TEST_DOWNLOAD_URL = "https://easypaysoftware.com/mobile.easypay5.com.cer"
        private const val WRONG_TEST_DOWNLOAD_URL = "https://google.com"
        private const val LOCAL_CERT_FILE_NAME = "mobile.easypay5.com.cer"
    }

    @Test
    fun `downloadFrom() returns proper file content`() = runTest {
        downloadManager.downloadFrom(TEST_DOWNLOAD_URL) { result ->
            val localCert = getLocalCert()
            assertEquals(localCert.contentToString(), result.contentToString())
        }
    }

    @Test
    fun `downloadFrom() with wrong URL doesn't return proper certificate`() = runTest {
        downloadManager.downloadFrom(WRONG_TEST_DOWNLOAD_URL) { result ->
            val localCert = getLocalCert()
            assertNotEquals(localCert.contentToString(), result.contentToString())
        }
    }
    //region Helpers

    private fun getLocalCert(): ByteArray? {
        val inputStream = object {}.javaClass.classLoader?.getResourceAsStream(LOCAL_CERT_FILE_NAME)
        return inputStream?.readBytes()
    }

    //endregion

}