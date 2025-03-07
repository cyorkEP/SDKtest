package com.easypaysolutions.utils.extensions

import android.content.Context

internal fun Int.dpToPx(context: Context): Int {
    return (this * context.resources.displayMetrics.density).toInt()
}