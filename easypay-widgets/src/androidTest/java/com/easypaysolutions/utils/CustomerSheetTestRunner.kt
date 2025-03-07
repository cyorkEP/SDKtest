package com.easypaysolutions.utils

import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import com.easypaysolutions.BuildConfig
import com.easypaysolutions.EasyPay
import com.easypaysolutions.customer_sheet.CustomerSheet
import com.easypaysolutions.customer_sheet.presentation.CustomerSheetActivity
import com.easypaysolutions.customer_sheet.utils.CustomerSheetResultCallback
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

internal class CustomerSheetTestRunnerContext(
    private val scenario: ActivityScenario<TestMainActivity>,
    private val customerSheet: CustomerSheet,
    private val countDownLatch: CountDownLatch,
) {
    fun presentCustomerSheet(block: CustomerSheet.() -> Unit) {
        val activityLaunchObserver = ActivityLaunchObserver(CustomerSheetActivity::class.java)
        scenario.onActivity {
            activityLaunchObserver.prepareForLaunch(it)
            customerSheet.block()
        }
        activityLaunchObserver.awaitLaunch()
    }

    /**
     * Normally we know a test succeeds when it calls [CustomerSheetResultCallback], but some tests
     * succeed based on other criteria. In these cases, call this method to manually mark a test as
     * succeeded.
     */
    fun markTestSucceeded() {
        countDownLatch.countDown()
    }
}

internal fun runCustomerSheetTest(
    timeoutSecondsLimit: Long = 10,
    resultCallback: CustomerSheetResultCallback,
    block: (CustomerSheetTestRunnerContext) -> Unit,
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

        lateinit var customerSheet: CustomerSheet
        scenario.onActivity {
            customerSheet = CustomerSheet(it) { result ->
                resultCallback.onCustomerSheetResult(result)
                countDownLatch.countDown()
            }
        }

        scenario.moveToState(Lifecycle.State.RESUMED)

        val testContext = CustomerSheetTestRunnerContext(scenario, customerSheet, countDownLatch)
        block(testContext)

        val didCompleteSuccessfully = countDownLatch.await(timeoutSecondsLimit, TimeUnit.SECONDS)
        assert(didCompleteSuccessfully)
    }
}
