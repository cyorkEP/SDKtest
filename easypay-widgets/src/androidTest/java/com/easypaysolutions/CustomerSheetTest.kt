package com.easypaysolutions

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.easypaysolutions.api.responses.annual_consent.CreateAnnualConsentResult
import com.easypaysolutions.customer_sheet.CustomerSheet
import com.easypaysolutions.repositories.annual_consent.create.ConsentCreatorParam
import com.easypaysolutions.utils.assertFailed
import com.easypaysolutions.utils.assertSelected
import com.easypaysolutions.utils.expectNoResult
import com.easypaysolutions.utils.pages.AddNewCardPage
import com.easypaysolutions.utils.pages.PaymentSheetPage
import com.easypaysolutions.utils.pages.PopupSheetPage
import com.easypaysolutions.utils.runCustomerSheetTest
import com.easypaysolutions.utils.sdk_helpers.AnnualConsentHelper
import com.easypaysolutions.widgets.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Date

@RunWith(AndroidJUnit4::class)
internal class CustomerSheetTest {

    companion object {
        private const val EXTENDED_TIMEOUT_FOR_TYPING = 30L
    }

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val testScope = CoroutineScope(Dispatchers.Default)

    private val page: PaymentSheetPage = PaymentSheetPage()
    private val popupSheetPage: PopupSheetPage = PopupSheetPage()
    private val addNewCardPage: AddNewCardPage = AddNewCardPage()

    private var createAnnualConsentResult: CreateAnnualConsentResult? = null

    //region Configuration

    private val builder: CustomerSheet.Configuration.Builder = CustomerSheet.Configuration
        .Builder()
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

    /**
     * This configuration will fail because merchantId is 0.
     */
    private val failedConfig = builder
        .setConsentCreator(
            ConsentCreatorParam(
                merchantId = 0,
                limitLifeTime = 10000.0,
                limitPerCharge = 1000.0,
                startDate = Date(),
                customerReferenceId = "TEST_ANDROID_CUSTOMER_REFERENCE_ID"
            )
        )
        .build()

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
    fun testSuccessfulCardSelection() = runCustomerSheetTest(
        resultCallback = ::assertSelected
    ) { testContext ->
        testScope.launch {
            createAnnualConsent()

            testContext.presentCustomerSheet {
                present(successfulConfig)
            }

            page.waitForCards()
            page.selectCard()
            page.clickOnCloseButton()
        }
    }

    @Test
    fun testSuccessfulNewCardSave() = runCustomerSheetTest(
        timeoutSecondsLimit = EXTENDED_TIMEOUT_FOR_TYPING,
        resultCallback = ::expectNoResult
    ) { testContext ->
        testScope.launch {
            testContext.presentCustomerSheet {
                present(successfulConfig)
            }

            page.waitForCards()
            page.navigateToAddNewCard()
            addNewCardPage.fillCardData()
            addNewCardPage.clickOnPrimaryActionButton()
            addNewCardPage.waitUntilIsGone()
            page.waitForCards()
            testContext.markTestSucceeded()
        }
    }

    @Test
    fun testFailedNewCardSave() = runCustomerSheetTest(
        timeoutSecondsLimit = EXTENDED_TIMEOUT_FOR_TYPING,
        resultCallback = ::expectNoResult
    ) { testContext ->
        testScope.launch {
            testContext.presentCustomerSheet {
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
    fun testSuccessfulDeleteSelectedCard() = runCustomerSheetTest(
        resultCallback = ::expectNoResult
    ) { testContext ->
        testScope.launch {
            createAnnualConsent()

            testContext.presentCustomerSheet {
                present(successfulConfig)
            }

            page.waitForCards()
            page.selectCard()
            page.clickOnDeleteButton()
            popupSheetPage.clickOnPrimaryActionButton()
            page.waitForSnackbarSuccess(R.string.delete_card_success_message)
            testContext.markTestSucceeded()
        }
    }

    @Test
    fun testFailedToOpenWidgetWithWrongConfiguration() = runCustomerSheetTest(
        resultCallback = ::assertFailed
    ) { testContext ->
        testScope.launch {
            testContext.presentCustomerSheet {
                present(failedConfig)
            }
        }
    }

    @Test
    fun testFailedToOpenWidgetWithNeitherRpguidOrCustomerRefId() = runCustomerSheetTest(
        resultCallback = ::assertFailed
    ) { testContext ->
        testScope.launch {
            testContext.presentCustomerSheet {
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