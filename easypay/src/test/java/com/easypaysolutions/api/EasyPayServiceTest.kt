package com.easypaysolutions.api

import com.easypaysolutions.api.responses.annual_consent.CancelAnnualConsentResponse
import com.easypaysolutions.api.responses.annual_consent.CreateAnnualConsentResponse
import com.easypaysolutions.api.responses.annual_consent.ListAnnualConsentsResponse
import com.easypaysolutions.api.responses.annual_consent.ProcessPaymentAnnualResponse
import com.easypaysolutions.api.responses.charge_cc.ChargeCreditCardResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
internal class EasyPayServiceTest {

    private lateinit var api: EasyPayService
    private val server = MockWebServer()
    private val gson: Gson = GsonBuilder().create()
    private val gsonConverterFactory = GsonConverterFactory.create(gson)

    @Before
    fun beforeEach() {
        api = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(gsonConverterFactory)
            .build()
            .create(EasyPayService::class.java)
    }

    @After
    fun afterEach() {
        server.shutdown()
    }

    //region ChargeCreditCard tests

    @Test
    fun `cardSaleManual() returns Success`() = runBlocking {
        val responseBody: ChargeCreditCardResponse = mock()
        val json = gson.toJson(responseBody)
        val response = MockResponse()
        response.setBody(json)
        server.enqueue(response)

        val data = api.cardSaleManual("sessKey", mock())
        server.takeRequest()

        assert(data.errorBody() == null)
        assertEquals(data.body(), responseBody)
        assert(data.code() == 200)
    }

    @Test
    fun `cardSaleManual() returns Error`() = runBlocking {
        val response = MockResponse()
        response.setResponseCode(404)
        server.enqueue(response)

        val data = api.cardSaleManual("sessKey", mock())
        server.takeRequest()

        assert(data.errorBody() != null)
        assert(data.isSuccessful.not())
        assertEquals(data.code(), 404)
    }

    //endregion

    //region CreateAnnualConsent tests

    @Test
    fun `createAnnualConsent() returns Success`() = runBlocking {
        val responseBody: CreateAnnualConsentResponse = mock()
        val json = gson.toJson(responseBody)
        val response = MockResponse()
        response.setBody(json)
        server.enqueue(response)

        val data = api.createAnnualConsent("sessKey", mock())
        server.takeRequest()

        assert(data.errorBody() == null)
        assertEquals(data.body(), responseBody)
        assert(data.code() == 200)
    }

    @Test
    fun `createAnnualConsent() returns Error`() = runBlocking {
        val response = MockResponse()
        response.setResponseCode(404)
        server.enqueue(response)

        val data = api.createAnnualConsent("sessKey", mock())
        server.takeRequest()

        assert(data.errorBody() != null)
        assert(data.isSuccessful.not())
        assertEquals(data.code(), 404)
    }

    //endregion

    //region ListAnnualConsents tests

    @Test
    fun `listAnnualConsents() returns Success`() = runBlocking {
        val responseBody: ListAnnualConsentsResponse = mock()
        val json = gson.toJson(responseBody)
        val response = MockResponse()
        response.setBody(json)
        server.enqueue(response)

        val data = api.listAnnualConsents("sessKey", mock())
        server.takeRequest()

        assert(data.errorBody() == null)
        assertEquals(data.body(), responseBody)
        assert(data.code() == 200)
    }

    @Test
    fun `listAnnualConsents() returns Error`() = runBlocking {
        val response = MockResponse()
        response.setResponseCode(404)
        server.enqueue(response)

        val data = api.listAnnualConsents("sessKey", mock())
        server.takeRequest()

        assert(data.errorBody() != null)
        assert(data.isSuccessful.not())
        assertEquals(data.code(), 404)
    }

    //endregion

    //region ListAnnualConsents tests

    @Test
    fun `cancelAnnualConsent() returns Success`() = runBlocking {
        val responseBody: CancelAnnualConsentResponse = mock()
        val json = gson.toJson(responseBody)
        val response = MockResponse()
        response.setBody(json)
        server.enqueue(response)

        val data = api.cancelAnnualConsent("sessKey", mock())
        server.takeRequest()

        assert(data.errorBody() == null)
        assertEquals(data.body(), responseBody)
        assert(data.code() == 200)
    }

    @Test
    fun `cancelAnnualConsent() returns Error`() = runBlocking {
        val response = MockResponse()
        response.setResponseCode(404)
        server.enqueue(response)

        val data = api.cancelAnnualConsent("sessKey", mock())
        server.takeRequest()

        assert(data.errorBody() != null)
        assert(data.isSuccessful.not())
        assertEquals(data.code(), 404)
    }

    //endregion

    //region ProcessPaymentAnnual tests

    @Test
    fun `processPaymentAnnual() returns Success`() = runBlocking {
        val responseBody: ProcessPaymentAnnualResponse = mock()
        val json = gson.toJson(responseBody)
        val response = MockResponse()
        response.setBody(json)
        server.enqueue(response)

        val data = api.processPaymentAnnual("sessKey", mock())
        server.takeRequest()

        assert(data.errorBody() == null)
        assertEquals(data.body(), responseBody)
        assert(data.code() == 200)
    }

    @Test
    fun `processPaymentAnnual() returns Error`() = runBlocking {
        val response = MockResponse()
        response.setResponseCode(404)
        server.enqueue(response)

        val data = api.processPaymentAnnual("sessKey", mock())
        server.takeRequest()

        assert(data.errorBody() != null)
        assert(data.isSuccessful.not())
        assertEquals(data.code(), 404)
    }

    //endregion


}