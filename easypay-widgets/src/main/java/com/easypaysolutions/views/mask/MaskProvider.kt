package com.easypaysolutions.views.mask

import com.google.android.material.textfield.TextInputEditText

internal class MaskProvider(
    mask: Mask,
    editText: TextInputEditText
) {
    val maskChangedListener = MaskChangedListener(mask)

    init {
        editText.addTextChangedListener(maskChangedListener)
    }

    fun getUnmaskedText(): String = maskChangedListener.unMasked
}