package com.easypaysolutions.repositories.annual_consent.process_payment

import com.easypaysolutions.networking.NetworkResource
import com.easypaysolutions.utils.TestApiHelper
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
internal class ProcessPaymentAnnualRepositoryTest {

    private val apiHelper = TestApiHelper()
    private val repository: ProcessPaymentAnnualRepository =
        ProcessPaymentAnnualRepositoryImpl(apiHelper)

    //region Tests

    @Test
    fun `processPaymentAnnual() with valid params returns Success`() = runBlocking {
        val params = prepareParams()
        val result = repository.processPaymentAnnual(params)
        TestCase.assertEquals(result.status, NetworkResource.Status.SUCCESS)
    }

    //endregion

    //region Private

    private fun prepareParams(): ProcessPaymentAnnualParams = ProcessPaymentAnnualParams(1, 5.0)

    //endregion

}