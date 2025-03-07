@file:Suppress("TYPEALIAS_EXPANSION_DEPRECATION")

package com.easypaysolutions

import android.content.Context
import com.easypaysolutions.api.ApiModule
import com.easypaysolutions.networking.NetworkingModule
import com.easypaysolutions.repositories.annual_consent.ConsentAnnualModule
import com.easypaysolutions.repositories.charge_cc.ChargeCreditCardModule
import com.easypaysolutions.utils.RootedDeviceValidator
import com.easypaysolutions.utils.feature_flags.EasyPayFeatureFlagManager
import io.sentry.Sentry
import io.sentry.android.core.SentryAndroid
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.error.ApplicationAlreadyStartedException

/**
 * Entry-point to the EasyPay SDK.
 */
class EasyPay private constructor() {

    companion object {

        //region Public

        fun init(
            context: Context,
            sessionKey: String,
            hmacSecret: String,
            sentryKey: String? = null,
        ) {
            initModules(context)
            initSentry(context, sentryKey)
            EasyPayConfiguration.init(sessionKey, hmacSecret)
            if (EasyPayFeatureFlagManager.isRootedDeviceDetectionEnabled()) {
                RootedDeviceValidator.verifyDevice(context)
            }
        }

        //endregion

        //region Private

        private fun initSentry(context: Context, sentryKey: String?) {
            if (sentryKey.isNullOrBlank()) return

            SentryAndroid.init(context) { options ->
                options.apply {
                    dsn = sentryKey
                    environment = "production"
                }
            }
        }

        private fun initModules(context: Context) {
            try {
                startKoin {
                    androidContext(context)
                    modules(
                        NetworkingModule.networkingModules,
                        ApiModule.apiModules,
                        ConsentAnnualModule.consentAnnualModules,
                        ChargeCreditCardModule.chargeCreditCardModules
                    )
                }
            } catch (e: ApplicationAlreadyStartedException) {
                Sentry.captureException(e)
                e.printStackTrace()
            }
        }

        //endregion
    }
}