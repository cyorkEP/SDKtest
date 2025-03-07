package com.easypaysolutions

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.easypaysolutions.networking.NetworkResource
import com.easypaysolutions.repositories.charge_cc.ChargeCreditCard
import com.easypaysolutions.utils.ChargeCreditCardHelper
import com.easypaysolutions.utils.secured.SecureTextField
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Thread.sleep

@RunWith(AndroidJUnit4::class)
internal class ChargeCreditCardEndToEndTest {

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

    //region Tests

    @Test
    fun chargeCreditCard_withProperParams_shouldReturnSuccess() = runBlocking {
        secureTextField.setText("4111111111111111")
        val params = ChargeCreditCardHelper.prepareParams(secureTextField.secureData)
        val result = ChargeCreditCard().chargeCreditCard(params)
        if (result.status == NetworkResource.Status.ERROR) {
            val error =
                "ERROR:64513:HIGH:This card has been successfully charged over 6 times in the last 24 hours"
            assertEquals(result.error?.message, error)
        } else {
            assertEquals(result.status, NetworkResource.Status.SUCCESS)
        }
    }

    @Test
    fun chargeCreditCard_withInvalidParams_shouldReturnError() = runBlocking {
        secureTextField.setText("4111111111111111")
        val params =
            ChargeCreditCardHelper.prepareParams(secureTextField.secureData, totalAmount = 0.0)
        val result = ChargeCreditCard().chargeCreditCard(params)
        assertEquals(result.error?.message, "totalAmount must be greater than 0.0")
    }

    //endregion

}