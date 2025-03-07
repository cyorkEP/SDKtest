package com.easypaysolutions.api

import com.easypaysolutions.api.requests.annual_consent.CancelAnnualConsentBodyDto
import com.easypaysolutions.api.requests.annual_consent.CancelAnnualConsentRequest
import com.easypaysolutions.api.requests.annual_consent.ConsentAnnualQuery
import com.easypaysolutions.api.requests.annual_consent.CreateAnnualConsentBodyDto
import com.easypaysolutions.api.requests.annual_consent.CreateAnnualConsentRequest
import com.easypaysolutions.api.requests.annual_consent.ListAnnualConsentsRequest
import com.easypaysolutions.api.requests.annual_consent.ProcessPaymentAnnualBodyDto
import com.easypaysolutions.api.requests.annual_consent.ProcessPaymentAnnualRequest
import com.easypaysolutions.api.requests.charge_cc.ChargeCreditCardBodyDto
import com.easypaysolutions.api.requests.charge_cc.ChargeCreditCardRequest
import com.easypaysolutions.api.responses.annual_consent.CancelAnnualConsentResponse
import com.easypaysolutions.api.responses.annual_consent.CancelAnnualConsentResult
import com.easypaysolutions.api.responses.annual_consent.CreateAnnualConsentResponse
import com.easypaysolutions.api.responses.annual_consent.CreateAnnualConsentResult
import com.easypaysolutions.api.responses.annual_consent.ListAnnualConsentsResponse
import com.easypaysolutions.api.responses.annual_consent.ListAnnualConsentsResult
import com.easypaysolutions.api.responses.annual_consent.ProcessPaymentAnnualResponse
import com.easypaysolutions.api.responses.annual_consent.ProcessPaymentAnnualResult
import com.easypaysolutions.api.responses.charge_cc.ChargeCreditCardResponse
import com.easypaysolutions.api.responses.charge_cc.ChargeCreditCardResult
import com.easypaysolutions.networking.DefaultNetworkDataSource
import com.easypaysolutions.networking.NetworkResource
import com.easypaysolutions.networking.authentication.AuthHelper
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Response

@RunWith(MockitoJUnitRunner::class)
internal class EasyPayApiHelperTest {

    private val authHelper = TestAuthHelper()
    private val networkDataSource = DefaultNetworkDataSource()
    private lateinit var easyPayApiHelper: EasyPayApiHelper

    //region ChargeCreditCard tests

    @Test
    fun `chargeCreditCard() returns Success`() = runBlocking {
        initHelperWith(TestSuccessEasyPayService())
        val result = executeChargeCreditCard()
        assertEquals(result.status, NetworkResource.Status.SUCCESS)
    }

    @Test
    fun `chargeCreditCard() returns Declined`() = runBlocking {
        initHelperWith(TestDeclinedEasyPayService())
        val result = executeChargeCreditCard()
        assertEquals(result.status, NetworkResource.Status.DECLINED)
    }

    @Test
    fun `chargeCreditCard() returns Error from API`() = runBlocking {
        initHelperWith(TestApiErrorEasyPayService())
        val result = executeChargeCreditCard()
        assertEquals(result.status, NetworkResource.Status.ERROR)
    }

    @Test
    fun `chargeCreditCard() returns Error from SDK`() = runBlocking {
        initHelperWith(TestSdkErrorEasyPayService())
        val result = executeChargeCreditCard()
        assertEquals(result.status, NetworkResource.Status.ERROR)
    }

    //endregion

    //region ChargeCreditCard tests

    @Test
    fun `createAnnualConsent() returns Success`() = runBlocking {
        initHelperWith(TestSuccessEasyPayService())
        val result = executeCreateAnnualConsent()
        assertEquals(result.status, NetworkResource.Status.SUCCESS)
    }

    @Test
    fun `createAnnualConsent() returns Error from API`() = runBlocking {
        initHelperWith(TestApiErrorEasyPayService())
        val result = executeCreateAnnualConsent()
        assertEquals(result.status, NetworkResource.Status.ERROR)
    }

    @Test
    fun `createAnnualConsent() returns Error from SDK`() = runBlocking {
        initHelperWith(TestSdkErrorEasyPayService())
        val result = executeCreateAnnualConsent()
        assertEquals(result.status, NetworkResource.Status.ERROR)
    }

    //endregion

    //region ListAnnualConsents tests

    @Test
    fun `listAnnualConsents() returns Success`() = runBlocking {
        initHelperWith(TestSuccessEasyPayService())
        val result = executeListAnnualConsents()
        assertEquals(result.status, NetworkResource.Status.SUCCESS)
    }

    @Test
    fun `listAnnualConsents() returns Error from API`() = runBlocking {
        initHelperWith(TestApiErrorEasyPayService())
        val result = executeListAnnualConsents()
        assertEquals(result.status, NetworkResource.Status.ERROR)
    }

    @Test
    fun `listAnnualConsents() returns Error from SDK`() = runBlocking {
        initHelperWith(TestSdkErrorEasyPayService())
        val result = executeListAnnualConsents()
        assertEquals(result.status, NetworkResource.Status.ERROR)
    }

    //endregion

    //region ListAnnualConsents tests

    @Test
    fun `cancelAnnualConsent() returns Success`() = runBlocking {
        initHelperWith(TestSuccessEasyPayService())
        val result = executeCancelAnnualConsent()
        assertEquals(result.status, NetworkResource.Status.SUCCESS)
    }

    @Test
    fun `cancelAnnualConsent() returns Error from API`() = runBlocking {
        initHelperWith(TestApiErrorEasyPayService())
        val result = executeCancelAnnualConsent()
        assertEquals(result.status, NetworkResource.Status.ERROR)
    }

    @Test
    fun `cancelAnnualConsent() returns Error from SDK`() = runBlocking {
        initHelperWith(TestSdkErrorEasyPayService())
        val result = executeCancelAnnualConsent()
        assertEquals(result.status, NetworkResource.Status.ERROR)
    }

    //endregion

    //region ProcessPaymentAnnual tests

    @Test
    fun `processPaymentAnnual() returns Success`() = runBlocking {
        initHelperWith(TestSuccessEasyPayService())
        val result = executeProcessPaymentAnnual()
        assertEquals(result.status, NetworkResource.Status.SUCCESS)
    }

    @Test
    fun `processPaymentAnnual() returns Declined`() = runBlocking {
        initHelperWith(TestDeclinedEasyPayService())
        val result = executeProcessPaymentAnnual()
        assertEquals(result.status, NetworkResource.Status.DECLINED)
    }

    @Test
    fun `processPaymentAnnual() returns Error from API`() = runBlocking {
        initHelperWith(TestApiErrorEasyPayService())
        val result = executeProcessPaymentAnnual()
        assertEquals(result.status, NetworkResource.Status.ERROR)
    }

    @Test
    fun `processPaymentAnnual() returns Error from SDK`() = runBlocking {
        initHelperWith(TestSdkErrorEasyPayService())
        val result = executeProcessPaymentAnnual()
        assertEquals(result.status, NetworkResource.Status.ERROR)
    }

    //endregion

    //region Helpers

    private suspend fun executeChargeCreditCard(): NetworkResource<ChargeCreditCardResult> {
        val request = ChargeCreditCardRequest(userDataPresent = true, mock())
        return easyPayApiHelper.chargeCreditCard(request)
    }

    private suspend fun executeCreateAnnualConsent(): NetworkResource<CreateAnnualConsentResult> {
        val request = CreateAnnualConsentRequest(userDataPresent = true, mock())
        return easyPayApiHelper.createAnnualConsent(request)
    }

    private suspend fun executeListAnnualConsents(): NetworkResource<ListAnnualConsentsResult> {
        val request = ListAnnualConsentsRequest(userDataPresent = true, mock())
        return easyPayApiHelper.listAnnualConsents(request)
    }

    private suspend fun executeCancelAnnualConsent(): NetworkResource<CancelAnnualConsentResult> {
        val request = CancelAnnualConsentRequest(userDataPresent = true, mock())
        return easyPayApiHelper.cancelAnnualConsent(request)
    }

    private suspend fun executeProcessPaymentAnnual(): NetworkResource<ProcessPaymentAnnualResult> {
        val request = ProcessPaymentAnnualRequest(userDataPresent = true, mock())
        return easyPayApiHelper.processPaymentAnnual(request)
    }

    private fun initHelperWith(easyPayService: EasyPayService) {
        easyPayApiHelper = EasyPayApiHelperImpl(
            easyPayService,
            networkDataSource,
            authHelper
        )
    }

    private class TestSdkErrorEasyPayService : EasyPayService {
        override suspend fun listAnnualConsents(
            sessKey: String,
            query: ConsentAnnualQuery,
        ): Response<ListAnnualConsentsResponse> {
            return Response.error(
                400,
                "{\"key\":[\"test\"]}".toResponseBody("application/json".toMediaTypeOrNull())
            )
        }

        override suspend fun cardSaleManual(
            sessKey: String,
            body: ChargeCreditCardBodyDto,
        ): Response<ChargeCreditCardResponse> {
            return Response.error(
                400,
                "{\"key\":[\"test\"]}".toResponseBody("application/json".toMediaTypeOrNull())
            )
        }

        override suspend fun createAnnualConsent(
            sessKey: String,
            body: CreateAnnualConsentBodyDto,
        ): Response<CreateAnnualConsentResponse> {
            return Response.error(
                400,
                "{\"key\":[\"test\"]}".toResponseBody("application/json".toMediaTypeOrNull())
            )
        }

        override suspend fun cancelAnnualConsent(
            sessKey: String,
            body: CancelAnnualConsentBodyDto,
        ): Response<CancelAnnualConsentResponse> {
            return Response.error(
                400,
                "{\"key\":[\"test\"]}".toResponseBody("application/json".toMediaTypeOrNull())
            )
        }

        override suspend fun processPaymentAnnual(
            sessKey: String,
            body: ProcessPaymentAnnualBodyDto,
        ): Response<ProcessPaymentAnnualResponse> {
            return Response.error(
                400,
                "{\"key\":[\"test\"]}".toResponseBody("application/json".toMediaTypeOrNull())
            )
        }
    }

    private class TestApiErrorEasyPayService : EasyPayService {
        override suspend fun listAnnualConsents(
            sessKey: String,
            query: ConsentAnnualQuery,
        ): Response<ListAnnualConsentsResponse> {
            val result = TestHelper.prepareListAnnualConsentsResult(
                functionOk = false
            )
            val response = ListAnnualConsentsResponse(result)
            return Response.success(response)
        }

        override suspend fun cardSaleManual(
            sessKey: String,
            body: ChargeCreditCardBodyDto,
        ): Response<ChargeCreditCardResponse> {
            val result = TestHelper.prepareChargeCardResult(
                functionOk = false,
                txApproved = false
            )
            val response = ChargeCreditCardResponse(result)
            return Response.success(response)
        }

        override suspend fun createAnnualConsent(
            sessKey: String,
            body: CreateAnnualConsentBodyDto,
        ): Response<CreateAnnualConsentResponse> {
            val result = TestHelper.prepareCreateAnnualConsentResult(
                functionOk = false
            )
            val response = CreateAnnualConsentResponse(result)
            return Response.success(response)
        }

        override suspend fun cancelAnnualConsent(
            sessKey: String,
            body: CancelAnnualConsentBodyDto,
        ): Response<CancelAnnualConsentResponse> {
            val result = TestHelper.prepareCancelAnnualConsentResult(
                functionOk = false
            )
            val response = CancelAnnualConsentResponse(result)
            return Response.success(response)
        }

        override suspend fun processPaymentAnnual(
            sessKey: String,
            body: ProcessPaymentAnnualBodyDto,
        ): Response<ProcessPaymentAnnualResponse> {
            val result = TestHelper.prepareProcessPaymentAnnualResult(
                functionOk = false,
                txApproved = false
            )
            val response = ProcessPaymentAnnualResponse(result)
            return Response.success(response)
        }
    }

    private class TestDeclinedEasyPayService : EasyPayService {
        override suspend fun listAnnualConsents(
            sessKey: String,
            query: ConsentAnnualQuery,
        ): Response<ListAnnualConsentsResponse> {
            return mock()   // not used
        }

        override suspend fun cardSaleManual(
            sessKey: String,
            body: ChargeCreditCardBodyDto,
        ): Response<ChargeCreditCardResponse> {
            val result = TestHelper.prepareChargeCardResult(
                functionOk = true,
                txApproved = false
            )
            val response = ChargeCreditCardResponse(result)
            return Response.success(response)
        }

        override suspend fun createAnnualConsent(
            sessKey: String,
            body: CreateAnnualConsentBodyDto,
        ): Response<CreateAnnualConsentResponse> {
            return mock()   // not used
        }

        override suspend fun cancelAnnualConsent(
            sessKey: String,
            body: CancelAnnualConsentBodyDto,
        ): Response<CancelAnnualConsentResponse> {
            return mock()   // not used
        }

        override suspend fun processPaymentAnnual(
            sessKey: String,
            body: ProcessPaymentAnnualBodyDto,
        ): Response<ProcessPaymentAnnualResponse> {
            val result = TestHelper.prepareProcessPaymentAnnualResult(
                functionOk = true,
                txApproved = false
            )
            val response = ProcessPaymentAnnualResponse(result)
            return Response.success(response)
        }
    }

    private class TestSuccessEasyPayService : EasyPayService {
        override suspend fun listAnnualConsents(
            sessKey: String,
            query: ConsentAnnualQuery,
        ): Response<ListAnnualConsentsResponse> {
            val result = TestHelper.prepareListAnnualConsentsResult()
            val response = ListAnnualConsentsResponse(result)
            return Response.success(response)
        }

        override suspend fun cardSaleManual(
            sessKey: String,
            body: ChargeCreditCardBodyDto,
        ): Response<ChargeCreditCardResponse> {
            val result = TestHelper.prepareChargeCardResult()
            val response = ChargeCreditCardResponse(result)
            return Response.success(response)
        }

        override suspend fun createAnnualConsent(
            sessKey: String,
            body: CreateAnnualConsentBodyDto,
        ): Response<CreateAnnualConsentResponse> {
            val result = TestHelper.prepareCreateAnnualConsentResult()
            val response = CreateAnnualConsentResponse(result)
            return Response.success(response)
        }

        override suspend fun cancelAnnualConsent(
            sessKey: String,
            body: CancelAnnualConsentBodyDto,
        ): Response<CancelAnnualConsentResponse> {
            val result = TestHelper.prepareCancelAnnualConsentResult()
            val response = CancelAnnualConsentResponse(result)
            return Response.success(response)
        }

        override suspend fun processPaymentAnnual(
            sessKey: String,
            body: ProcessPaymentAnnualBodyDto,
        ): Response<ProcessPaymentAnnualResponse> {
            val result = TestHelper.prepareProcessPaymentAnnualResult()
            val response = ProcessPaymentAnnualResponse(result)
            return Response.success(response)
        }
    }

    private class TestAuthHelper : AuthHelper {
        override fun getSessKey(userDataPresent: Boolean): String = "test"

        override fun getHmacHash(
            sessionKey: String,
            epoch: String,
            deviceId: String,
            hmacSecret: String,
        ): String = "test"
    }

    private object TestHelper {
        fun prepareChargeCardResult(
            functionOk: Boolean = true,
            txApproved: Boolean = true,
        ): ChargeCreditCardResult {
            return ChargeCreditCardResult(
                functionOk = functionOk,
                errorCode = 0,
                errorMessage = "",
                responseMessage = "APPROVED OK4501",
                txApproved = txApproved,
                txId = 64,
                txCode = "OK4501",
                avsResult = "Y",
                acquirerResponseEmv = null,
                cvvResult = "",
                isPartialApproval = false,
                requiresVoiceAuth = false,
                responseApprovedAmount = 0.0,
                responseAuthorizedAmount = 0.0,
                responseBalanceAmount = 0.0
            )
        }

        fun prepareProcessPaymentAnnualResult(
            functionOk: Boolean = true,
            txApproved: Boolean = true,
        ): ProcessPaymentAnnualResult {
            return ProcessPaymentAnnualResult(
                functionOk = functionOk,
                errorCode = 0,
                errorMessage = "",
                responseMessage = "APPROVED OK4501",
                txApproved = txApproved,
                txId = 64,
                txCode = "OK4501",
                avsResult = "Y",
                acquirerResponseEmv = null,
                cvvResult = "",
                isPartialApproval = false,
                requiresVoiceAuth = false,
                responseApprovedAmount = 0.0,
                responseAuthorizedAmount = 0.0,
                responseBalanceAmount = 0.0
            )
        }

        fun prepareCreateAnnualConsentResult(
            functionOk: Boolean = true,
        ): CreateAnnualConsentResult {
            return CreateAnnualConsentResult(
                functionOk = functionOk,
                errorCode = 0,
                errorMessage = "",
                responseMessage = "",
                consentId = 0,
                creationSuccess = functionOk,
                preConsentAuthMessage = "",
                preConsentAuthSuccess = functionOk,
                preConsentAuthTxId = 0
            )
        }

        fun prepareListAnnualConsentsResult(
            functionOk: Boolean = true,
        ): ListAnnualConsentsResult {
            return ListAnnualConsentsResult(
                functionOk = functionOk,
                errorCode = 0,
                errorMessage = "",
                responseMessage = "",
                numRecords = 0,
                consents = emptyList()
            )
        }

        fun prepareCancelAnnualConsentResult(
            functionOk: Boolean = true,
        ): CancelAnnualConsentResult {
            return CancelAnnualConsentResult(
                functionOk = functionOk,
                errorCode = 0,
                errorMessage = "",
                responseMessage = "",
                cancelSuccess = functionOk,
                cancelledConsentId = 0
            )
        }
    }

    //endregion

}