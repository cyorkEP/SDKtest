package com.easypaysolutions.views.mask

import android.text.Editable
import android.text.TextWatcher

@Suppress("SpellCheckingInspection")
internal class MaskChangedListener(mask: Mask) : TextWatcher {

    private val maskara: Maskara = Maskara(mask)

    private var selfChange: Boolean = false
    private var result: MaskResult? = null

    val unMasked: String
        get() = result?.unMasked.orEmpty()

    override fun afterTextChanged(s: Editable?) {
        if (selfChange || s == null) return

        selfChange = true
        result?.apply(s)
        selfChange = false
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        //does nothing
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (selfChange || s == null) return

        val action = if (before > 0 && count == 0) Action.DELETE else Action.INSERT
        result = maskara.apply(s, action)
    }
}
