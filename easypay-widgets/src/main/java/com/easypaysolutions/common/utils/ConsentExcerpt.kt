package com.easypaysolutions.common.utils
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ConsentExcerpt (val consentId: Int,
                           val expirationMonth: Int,
                           val expirationYear: Int,
                           val last4digits: String): Parcelable