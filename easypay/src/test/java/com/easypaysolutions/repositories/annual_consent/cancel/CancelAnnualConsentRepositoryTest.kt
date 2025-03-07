package com.easypaysolutions.repositories.annual_consent.cancel

import com.easypaysolutions.networking.NetworkResource
import com.easypaysolutions.utils.TestApiHelper
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
internal class CancelAnnualConsentRepositoryTest {

    private val apiHelper = TestApiHelper()
    private val repository: CancelAnnualConsentRepository =
        CancelAnnualConsentRepositoryImpl(apiHelper)

    //region Tests

    @Test
    fun `cancelAnnualConsent() with valid params returns Success`() = runBlocking {
        val params = prepareParams()
        val result = repository.cancelAnnualConsent(params)
        TestCase.assertEquals(result.status, NetworkResource.Status.SUCCESS)
    }

    //endregion

    //region Private

    private fun prepareParams(): CancelAnnualConsentBodyParams = CancelAnnualConsentBodyParams(1)

    //endregion

}