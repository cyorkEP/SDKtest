package com.easypaysolutions.utils

import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import com.easypaysolutions.BuildConfig
import com.easypaysolutions.EasyPay
import com.easypaysolutions.payment_sheet.PaymentSheet
import com.easypaysolutions.payment_sheet.presentation.PaymentSheetActivity
import com.easypaysolutions.payment_sheet.utils.PaymentSheetResultCallback
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

internal class PaymentSheetTestRunnerContext(
    private val scenario: ActivityScenario<TestMainActivity>,
    private val paymentSheet: PaymentSheet,
    private val countDownLatch: CountDownLatch,
) {
    fun presentPaymentSheet(block: PaymentSheet.() -> Unit) {
        val activityLaunchObserver = ActivityLaunchObserver(PaymentSheetActivity::class.java)
        scenario.onActivity {
            activityLaunchObserver.prepareForLaunch(it)
            paymentSheet.block()
        }
        activityLaunchObserver.awaitLaunch()
    }

    /**
     * Normally we know a test succeeds when it calls [PaymentSheetResultCallback], but some tests
     * succeed based on other criteria. In these cases, call this method to manually mark a test as
     * succeeded.
     */
    fun markTestSucceeded() {
        countDownLatch.countDown()
    }
}

internal fun runPaymentSheetTest(
    timeoutSecondsLimit: Long = 10,
    resultCallback: PaymentSheetResultCallback,
    block: (PaymentSheetTestRunnerContext) -> Unit,
) {
    val countDownLatch = CountDownLatch(1)

    ActivityScenario.launch(TestMainActivity::class.java).use { scenario ->
        scenario.moveToState(Lifecycle.State.CREATED)
        scenario.onActivity {
            EasyPay.init(
                it.applicationContext,
                BuildConfig.SESSION_KEY_FOR_TESTS,
                BuildConfig.HMAC_SECRET_FOR_TESTS
            )
        }

        lateinit var paymentSheet: PaymentSheet
        scenario.onActivity {
            paymentSheet = PaymentSheet(it) { result ->
                resultCallback.onPaymentSheetResult(result)
                countDownLatch.countDown()
            }
        }

        scenario.moveToState(Lifecycle.State.RESUMED)

        val testContext = PaymentSheetTestRunnerContext(scenario, paymentSheet, countDownLatch)
        block(testContext)

        val didCompleteSuccessfully = countDownLatch.await(timeoutSecondsLimit, TimeUnit.SECONDS)
        assert(didCompleteSuccessfully)
    }
}
