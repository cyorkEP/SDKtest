package com.easypaysolutions

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.easypaysolutions.api.responses.annual_consent.CreateAnnualConsentResult
import com.easypaysolutions.payment_sheet.PaymentSheet
import com.easypaysolutions.repositories.annual_consent.create.ConsentCreatorParam
import com.easypaysolutions.repositories.charge_cc.AmountsParam
import com.easypaysolutions.utils.assertCompleted
import com.easypaysolutions.utils.assertFailed
import com.easypaysolutions.utils.expectNoResult
import com.easypaysolutions.utils.pages.AddNewCardPage
import com.easypaysolutions.utils.pages.PaymentSheetPage
import com.easypaysolutions.utils.pages.PopupSheetPage
import com.easypaysolutions.utils.runCustomerSheetTest
import com.easypaysolutions.utils.runPaymentSheetTest
import com.easypaysolutions.utils.sdk_helpers.AnnualConsentHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Date

@RunWith(AndroidJUnit4::class)
internal class PaymentSheetTest {

    companion object {
        private const val EXTENDED_TIMEOUT = 15L
        private const val EXTENDED_TIMEOUT_FOR_TYPING = 30L
    }

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val testScope = CoroutineScope(Dispatchers.Default)

    private val page: PaymentSheetPage = PaymentSheetPage()
    private val popupSheetPage: PopupSheetPage = PopupSheetPage()
    private val addNewCardPage: AddNewCardPage = AddNewCardPage()

    private var createAnnualConsentResult: CreateAnnualConsentResult? = null

    //region Configuration

    private val builder: PaymentSheet.Configuration.Builder = PaymentSheet.Configuration
        .Builder()
        .setAmounts(AmountsParam(1000.0))
        .setConsentCreator(
            ConsentCreatorParam(
                merchantId = 1,
                limitLifeTime = 10000.0,
                limitPerCharge = 1000.0,
                startDate = Date(),
                customerReferenceId = "TEST_ANDROID_CUSTOMER_REFERENCE_ID"
            )
        )

    private val successfulConfig = builder.build()

    //endregion

    //region Setup & TearDown

    @After
    fun tearDown() {
        testScope.launch {
            createAnnualConsentResult?.let {
                cancelAnnualConsent(it.consentId)
            }
        }
    }

    //endregion

    //region Tests

    @Test
    fun testSuccessfulSelectedCardPayment() = runPaymentSheetTest(
        timeoutSecondsLimit = EXTENDED_TIMEOUT,
        resultCallback = ::assertCompleted
    ) { testContext ->
        testScope.launch {
            createAnnualConsent()

            testContext.presentPaymentSheet {
                present(successfulConfig)
            }

            page.waitForCards()
            page.selectCard()
            page.clickOnCompleteButton()
        }
    }

    @Test
    fun testFailedSelectedCardPayment() = runPaymentSheetTest(
        resultCallback = ::expectNoResult
    ) { testContext ->
        testScope.launch {
            createAnnualConsent()

            // This configuration will fail because total amount is greater than the limit per charge.
            val failedConfig = builder
                .setAmounts(AmountsParam(1000000.0))
                .build()

            testContext.presentPaymentSheet {
                present(failedConfig)
            }

            page.waitForCards()
            page.selectCard()
            page.clickOnCompleteButton()
            page.waitForGeneralErrorText()
            testContext.markTestSucceeded()
        }
    }

    @Test
    fun testSuccessfulNewCardPayment() = runPaymentSheetTest(
        timeoutSecondsLimit = EXTENDED_TIMEOUT_FOR_TYPING,
        resultCallback = ::assertCompleted
    ) { testContext ->
        testScope.launch {
            testContext.presentPaymentSheet {
                present(successfulConfig)
            }

            page.waitForCards()
            page.navigateToAddNewCard()
            addNewCardPage.fillCardData()
            addNewCardPage.clickOnPrimaryActionButton()
        }
    }

    @Test
    fun testFailedNewCardPayment() = runPaymentSheetTest(
        timeoutSecondsLimit = EXTENDED_TIMEOUT_FOR_TYPING,
        resultCallback = ::expectNoResult
    ) { testContext ->
        testScope.launch {
            testContext.presentPaymentSheet {
                present(successfulConfig)
            }

            page.waitForCards()
            page.navigateToAddNewCard()

            val invalidCardNumber = "0000 0000 0000 0000"
            addNewCardPage.fillCardData(invalidCardNumber)
            addNewCardPage.clickOnPrimaryActionButton()
            addNewCardPage.waitForErrorText()
            testContext.markTestSucceeded()
        }
    }

    @Test
    fun testSuccessfulDeleteSelectedCard() = runPaymentSheetTest(
        resultCallback = ::expectNoResult
    ) { testContext ->
        testScope.launch {
            createAnnualConsent()

            testContext.presentPaymentSheet {
                present(successfulConfig)
            }

            page.waitForCards()
            page.selectCard()
            page.clickOnDeleteButton()
            popupSheetPage.clickOnPrimaryActionButton()
            page.waitForSnackbarSuccess(com.easypaysolutions.widgets.R.string.delete_card_success_message)
            testContext.markTestSucceeded()
        }
    }

    @Test
    fun testFailedToOpenWidgetWithWrongConfiguration() = runPaymentSheetTest(
        resultCallback = ::assertFailed
    ) { testContext ->
        testScope.launch {
            // This configuration will fail because total amount is 0.
            val failedConfig = builder
                .setAmounts(AmountsParam(0.0))
                .build()

            testContext.presentPaymentSheet {
                present(failedConfig)
            }
        }
    }


    @Test
    fun testFailedToOpenWidgetWithNeitherRpguidOrCustomerRefId() = runPaymentSheetTest(
        resultCallback = ::assertFailed
    ) { testContext ->
        testScope.launch {
            testContext.presentPaymentSheet {
                val failedConfig = builder
                    .setConsentCreator(
                        ConsentCreatorParam(
                            merchantId = 1,
                            limitLifeTime = 10000.0,
                            limitPerCharge = 1000.0,
                            startDate = Date()
                        )
                    )
                    .build()
                present(failedConfig)
            }
        }
    }

    //endregion

    //region Helpers

    private suspend fun createAnnualConsent() {
        createAnnualConsentResult = AnnualConsentHelper.createAnnualConsent(context)
    }

    private suspend fun cancelAnnualConsent(consentId: Int) {
        AnnualConsentHelper.cancelAnnualConsent(consentId)
        createAnnualConsentResult = null
    }

    //endregion

}