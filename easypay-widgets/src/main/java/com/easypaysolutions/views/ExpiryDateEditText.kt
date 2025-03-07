package com.easypaysolutions.views

import android.content.Context
import android.util.AttributeSet
import com.easypaysolutions.utils.date.DateUtils
import com.easypaysolutions.utils.validation.Validation
import com.easypaysolutions.utils.validation.ValidationRule
import com.easypaysolutions.views.mask.Mask
import com.easypaysolutions.views.mask.MaskConstants
import com.easypaysolutions.views.mask.MaskProvider
import com.easypaysolutions.views.mask.MaskStyle
import com.easypaysolutions.widgets.R

internal class ExpiryDateEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : EasyPayEditText(context, attrs, defStyleAttr) {

    private lateinit var maskProvider: MaskProvider

    init {
        setLabel(context.getString(R.string.expiry_month_and_year))
        setNumberOnlyInputType()
        setupMask()
    }

    //region Overridden

    override fun prepareValidationRules() {
        super.prepareValidationRules()
        setValidExpDateFormatValidationRule()
        setNotExpiredDateValidationRule()
    }

    //endregion

    //region Public

    fun getExpMonth(): String {
        return DateUtils.getMonthFromExpirationDate(getText())
    }

    fun getExpYear(): String {
        return DateUtils.getYearFromExpirationDate(getText())
    }

    //endregion

    //region Validation rules

    private fun setValidExpDateFormatValidationRule() {
        val rule = ValidationRule(
            isValid = Validation.isExpirationDateFormatValid(getText()),
            error = context.getString(R.string.invalid_expiry_date)
        )
        addValidationRule(rule)
    }

    private fun setNotExpiredDateValidationRule() {
        val rule = ValidationRule(
            isValid = Validation.isExpirationDateNotExpired(getText()),
            error = context.getString(R.string.card_expired)
        )
        addValidationRule(rule)
    }

    //endregion

    //region Helpers

    private fun setupMask() {
        val mask = Mask(
            value = MaskConstants.EXPIRATION_DATE_MASK,
            character = '_',
            style = MaskStyle.NORMAL
        )

        maskProvider = MaskProvider(mask, binding.editText)
    }

    //endregion

}