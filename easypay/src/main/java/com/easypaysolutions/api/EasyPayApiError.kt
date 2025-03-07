package com.easypaysolutions.api

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

/**
 * A model representing an EasyPay API errors object.
 */
@Parcelize
data class EasyPayApiError(
    val code: Int,
    val message: String,
) : Parcelable, Serializable