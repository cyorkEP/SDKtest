package com.easypaysolutions.views

import android.content.Context
import android.util.AttributeSet
import com.easypaysolutions.utils.validation.ValidationState
import com.easypaysolutions.widgets.R
import com.google.android.material.button.MaterialButton

internal class PrimaryButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : MaterialButton(context, attrs, defStyleAttr) {

    //region UI updates

    fun updateState(state: ValidationState) {
        when (state) {
            ValidationState.InternalValidationInvalid -> setNotFilledState()
            ValidationState.NotFilled -> setNotFilledState()
            ValidationState.Valid -> setValidState()
            ValidationState.ExternalValidationInvalid -> setInvalidState()
        }
    }

    //endregion

    //region State updates

    private fun setValidState() {
        updateButton(
            isEnabled = true,
            textColor = R.color.easypay_widget_button_action_foreground,
            backgroundTintList = R.color.easypay_widget_button_action_background
        )
    }

    private fun setNotFilledState() {
        updateButton(
            isEnabled = false,
            textColor = R.color.easypay_widget_button_action_foreground_disabled,
            backgroundTintList = R.color.easypay_widget_button_action_background_disabled
        )
    }

    private fun setInvalidState() {
        updateButton(
            isEnabled = false,
            textColor = R.color.easypay_widget_button_action_foreground_error,
            backgroundTintList = R.color.easypay_widget_button_action_background_error
        )
    }

    private fun updateButton(
        isEnabled: Boolean,
        textColor: Int,
        backgroundTintList: Int,
    ) {
        this.isEnabled = isEnabled
        setTextColor(context.getColor(textColor))
        this.backgroundTintList = resources.getColorStateList(
            backgroundTintList,
            context.theme
        )
    }

    //endregion

}