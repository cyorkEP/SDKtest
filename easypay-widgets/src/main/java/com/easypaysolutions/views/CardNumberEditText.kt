package com.easypaysolutions.views

import android.content.Context
import android.util.AttributeSet
import com.easypaysolutions.utils.extensions.showOnlyLastDigits
import com.easypaysolutions.utils.secured.SecureData
import com.easypaysolutions.utils.secured.SecureTextField
import com.easypaysolutions.views.mask.Mask
import com.easypaysolutions.views.mask.MaskConstants.CARD_NUMBER_MASK
import com.easypaysolutions.views.mask.MaskProvider
import com.easypaysolutions.views.mask.MaskStyle
import com.easypaysolutions.widgets.R

internal class CardNumberEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : EasyPayEditText(context, attrs, defStyleAttr) {

    private lateinit var maskProvider: MaskProvider

    companion object {
        private const val CARD_NUMBER_MIN_LENGTH = 13
    }

    init {
        setLabel(context.getString(R.string.card_number))
        setStartIconDrawable(R.drawable.ic_credit_card_outlined)
        setNumberOnlyInputType()
        setupMask()
    }

    //region Overridden

    override fun prepareValidationRules() {
        super.prepareValidationRules()
        setMinLengthValidationRule(CARD_NUMBER_MIN_LENGTH, R.string.invalid_card_number)
    }

    override fun getText(): String {
        return maskProvider.getUnmaskedText()
    }

    override fun setInternalOnFocusChangeListener(hasFocus: Boolean) {
        super.setInternalOnFocusChangeListener(hasFocus)
        if (hasFocus || isErrorStateDisplayed) {
            hideAsteriskMask()
            return
        }
        showAsteriskMask()
    }

    //endregion

    //region Public methods

    fun getSecureData(): SecureData<String> {
        val secureTextField = SecureTextField(context)
        secureTextField.setText(getText())
        return secureTextField.secureData
    }

    //endregion

    //region Helpers

    private fun setupMask() {
        val mask = Mask(
            value = CARD_NUMBER_MASK,
            character = '_',
            style = MaskStyle.NORMAL
        )

        maskProvider = MaskProvider(mask, binding.editText)
    }

    private fun hideAsteriskMask() {
        binding.editText.setText(getText())
    }

    private fun showAsteriskMask() {
        binding.editText.apply {
            removeTextChangedListener(maskProvider.maskChangedListener)
            showOnlyLastDigits(numberOfVisibleLastDigits = 4)
            addTextChangedListener(maskProvider.maskChangedListener)
        }
    }

    //endregion

}