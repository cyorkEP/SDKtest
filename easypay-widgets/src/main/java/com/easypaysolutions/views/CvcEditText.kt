package com.easypaysolutions.views

import android.content.Context
import android.util.AttributeSet
import com.easypaysolutions.widgets.R

internal class CvcEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : EasyPayEditText(context, attrs, defStyleAttr) {

    companion object {
        private const val CVC_MAX_LENGTH = 4
        private const val CVC_MIN_LENGTH = 3
    }

    init {
        setLabel(context.getString(R.string.cvc))
        setMaxLengthFilter(CVC_MAX_LENGTH)
        setPasswordMode()
    }

    override fun prepareValidationRules() {
        super.prepareValidationRules()
        setMinLengthValidationRule(CVC_MIN_LENGTH, R.string.invalid_cvc)
    }
}