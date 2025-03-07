package com.easypaysolutions

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.easypaysolutions.api.responses.annual_consent.CreateAnnualConsentResult
import com.easypaysolutions.networking.NetworkResource
import com.easypaysolutions.repositories.annual_consent.cancel.CancelAnnualConsent
import com.easypaysolutions.repositories.annual_consent.cancel.CancelAnnualConsentBodyParams
import com.easypaysolutions.repositories.annual_consent.create.CreateAnnualConsent
import com.easypaysolutions.repositories.annual_consent.list.ListAnnualConsents
import com.easypaysolutions.repositories.annual_consent.list.ListAnnualConsentsBodyParams
import com.easypaysolutions.repositories.annual_consent.process_payment.ProcessPaymentAnnual
import com.easypaysolutions.repositories.annual_consent.process_payment.ProcessPaymentAnnualParams
import com.easypaysolutions.utils.AnnualConsentHelper
import com.easypaysolutions.utils.secured.SecureTextField
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Thread.sleep

@RunWith(AndroidJUnit4::class)
internal class AnnualConsentsEndToEndTest {

    private lateinit var secureTextField: SecureTextField

    @Before
    fun setUp() {
        val context: Context = ApplicationProvider.getApplicationContext()
        context.setTheme(com.google.android.material.R.style.Theme_AppCompat)
        secureTextField = SecureTextField(context)
        EasyPay.init(
            context,
            BuildConfig.SESSION_KEY_FOR_TESTS,
            BuildConfig.HMAC_SECRET_FOR_TESTS
        )
        sleep(3000) // Additional time for certificate downloading
    }

    //region Create Annual Consent

    @Test
    fun createAnnualConsent_withProperParams_shouldReturnSuccess() = runBlocking {
        val result = createAnnualConsent()
        assertEquals(result.status, NetworkResource.Status.SUCCESS)
        assertNotNull(result.data)

        //cleanup
        cancelAnnualConsent(result.data!!.consentId)
    }

    @Test
    fun createAnnualConsent_withInvalidParams_shouldReturnError() = runBlocking {
        secureTextField.setText("4111111111111111")
        val params = AnnualConsentHelper.prepareParams(secureTextField.secureData, csv = "")
        val result = CreateAnnualConsent().createAnnualConsent(params)
        assertEquals(result.error?.message, "csv cannot be blank")
    }

    //endregion

    //region Process Payment Annual

    @Test
    fun processPaymentAnnual_withProperParams_shouldReturnSuccess() = runBlocking {
        val createNewConsentResult = createAnnualConsent()
        assertNotNull(createNewConsentResult.data)
        val params = ProcessPaymentAnnualParams(
            createNewConsentResult.data!!.consentId,
            processAmount = 10.0
        )
        val result = ProcessPaymentAnnual().processPaymentAnnual(params)
        assertEquals(result.status, NetworkResource.Status.SUCCESS)

        //cleanup
        cancelAnnualConsent(createNewConsentResult.data!!.consentId)
    }

    @Test
    fun processPaymentAnnual_withInvalidConsentId_shouldReturnError() = runBlocking {
        val params = ProcessPaymentAnnualParams(100000, 10.0)
        val result = ProcessPaymentAnnual().processPaymentAnnual(params)
        assertEquals(
            result.error?.message,
            "ERROR:6126:HIGH:Cannot get consent annual full detail for ID : 100000"
        )
    }

    @Test
    fun processPaymentAnnual_withInvalidProcessAmount_shouldReturnError() = runBlocking {
        val createNewConsentResult = createAnnualConsent()
        assertNotNull(createNewConsentResult.data)
        val params = ProcessPaymentAnnualParams(
            createNewConsentResult.data!!.consentId,
            processAmount = 0.0
        )
        val result = ProcessPaymentAnnual().processPaymentAnnual(params)
        assertEquals(result.error?.message, "processAmount must be greater than 0.0")

        //cleanup
        cancelAnnualConsent(createNewConsentResult.data!!.consentId)
    }

    //endregion

    //region List Annual Consents

    @Test
    fun listAnnualConsents_withProperParams_shouldReturnSuccess() = runBlocking {
        val params = ListAnnualConsentsBodyParams(
            merchantId = 1000,
            customerReferenceId = "Android_test_custom_ref_id"
        )
        val result = ListAnnualConsents().listAnnualConsents(params)
        assertEquals(result.status, NetworkResource.Status.SUCCESS)
    }

    //endregion

    //region Cancel Annual Consent

    @Test
    fun cancelAnnualConsent_withProperParams_shouldReturnSuccess() = runBlocking {
        val createNewConsentResult = createAnnualConsent()
        assertNotNull(createNewConsentResult.data)
        val params = CancelAnnualConsentBodyParams(createNewConsentResult.data!!.consentId)
        val result = CancelAnnualConsent().cancelAnnualConsent(params)
        assertEquals(result.status, NetworkResource.Status.SUCCESS)

        //cleanup
        cancelAnnualConsent(createNewConsentResult.data!!.consentId)
    }

    @Test
    fun cancelAnnualConsent_withInvalidConsentId_shouldReturnError() = runBlocking {
        val params = CancelAnnualConsentBodyParams(100000)
        val result = CancelAnnualConsent().cancelAnnualConsent(params)
        assertEquals(
            result.error?.message,
            "ERROR:5745:HIGH:Consent Annual ID = 100000 Does NOT exist"
        )
    }

    //endregion

    //region Helper methods

    private suspend fun cancelAnnualConsent(consentId: Int) {
        val params = CancelAnnualConsentBodyParams(consentId)
        CancelAnnualConsent().cancelAnnualConsent(params)
    }

    private suspend fun createAnnualConsent(): NetworkResource<CreateAnnualConsentResult> {
        secureTextField.setText("4111111111111111")
        val params = AnnualConsentHelper.prepareParams(secureTextField.secureData)
        return CreateAnnualConsent().createAnnualConsent(params)
    }

    //endregion

}