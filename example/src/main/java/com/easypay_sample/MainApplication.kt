package com.easypay_sample

import android.app.Application
import com.easypaysolutions.EasyPay
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initEasyPay()
    }

    private fun initEasyPay() {
        val sessionKey = BuildConfig.SESSION_KEY
        val hmacSecret = BuildConfig.HMAC_SECRET
        val sentryDsn = BuildConfig.SENTRY_DSN
        EasyPay.init(this.applicationContext, sessionKey, hmacSecret, sentryDsn)
    }
}