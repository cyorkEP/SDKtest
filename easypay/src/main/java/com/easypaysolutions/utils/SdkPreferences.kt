package com.easypaysolutions.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

internal class SdkPreferences(val context: Context?) {

    companion object {
        private const val PREF_NAME = "EasyPaySDKPreferences"
        private const val LAST_RSA_CERTIFICATE_FETCH_KEY = "LAST_RSA_CERTIFICATE_FETCH_KEY"
    }

    private val sharedPreferences: SharedPreferences? =
        context?.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE)

    //region RSA certificate

    fun setLastRsaCertificateFetch(timestamp: Long) {
        sharedPreferences?.edit()?.putLong(LAST_RSA_CERTIFICATE_FETCH_KEY, timestamp)?.apply()
    }

    fun getLastRsaCertificateFetch(): Long {
        return sharedPreferences?.getLong(LAST_RSA_CERTIFICATE_FETCH_KEY, 0L) ?: 0L
    }

    //endregion
}