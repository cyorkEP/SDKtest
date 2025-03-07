package com.easypaysolutions.repositories.annual_consent.list

import com.easypaysolutions.networking.NetworkResource
import com.easypaysolutions.utils.AnnualConsentHelper
import com.easypaysolutions.utils.TestApiHelper
import com.easypaysolutions.utils.validation.ValidationErrorMessages.EITHER_RPGUID_OR_CUSTOMER_REFERENCE_ID
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
internal class ListAnnualConsentsRepositoryTest {

    private val apiHelper = TestApiHelper()
    private val validator = ListAnnualConsentsValidator()
    private val repository: ListAnnualConsentsRepository =
        ListAnnualConsentsRepositoryImpl(apiHelper, validator)

    //region Tests

    @Test
    fun `listAnnualConsents() with valid params returns Success`() = runBlocking {
        val params = prepareParams()
        val result = repository.listAnnualConsents(params)
        TestCase.assertEquals(result.status, NetworkResource.Status.SUCCESS)
    }

    @Test
    fun `listAnnualConsents() with neither RPGUID or CustomerRefId returns Error`() =
        runBlocking {
            val params = prepareParams(customerRefId = "", rpguid = "")
            val result = repository.listAnnualConsents(params)
            TestCase.assertEquals(result.status, NetworkResource.Status.ERROR)
            TestCase.assertEquals(result.error?.message, EITHER_RPGUID_OR_CUSTOMER_REFERENCE_ID)
        }

    //endregion

    //region Private

    private fun prepareParams(
        customerRefId: String = "123456",
        rpguid: String? = null,
    ): ListAnnualConsentsBodyParams = ListAnnualConsentsBodyParams(
        merchantId = 1,
        customerReferenceId = customerRefId,
        rpguid = rpguid,
    )

    //endregion

}