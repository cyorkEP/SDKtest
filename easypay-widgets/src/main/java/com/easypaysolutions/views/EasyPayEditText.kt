package com.easypaysolutions.views

import android.content.Context
import android.text.InputFilter
import android.text.InputType
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doAfterTextChanged
import com.easypaysolutions.utils.AsteriskPasswordTransformationMethod
import com.easypaysolutions.utils.extensions.addPasswordEndIconClickListener
import com.easypaysolutions.utils.extensions.hideKeyboard
import com.easypaysolutions.utils.extensions.setDefaultHintColor
import com.easypaysolutions.utils.validation.Validation
import com.easypaysolutions.utils.validation.ValidationRule
import com.easypaysolutions.utils.validation.ValidationState
import com.easypaysolutions.widgets.R
import com.easypaysolutions.widgets.databinding.LayoutEasyPayEditTextBinding
import com.google.android.material.textfield.TextInputLayout

internal open class EasyPayEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val MAX_LENGTH_NOT_GIVEN = 0
    }

    protected val binding: LayoutEasyPayEditTextBinding = LayoutEasyPayEditTextBinding.inflate(
        LayoutInflater.from(context), this, true
    )

    protected var isErrorStateDisplayed: Boolean = false
    private var onLostFocusListener: OnLostFocusListener? = null
    private val validationRules = mutableListOf<ValidationRule>()
    private var regexToValidate: String? = null
    private var regexErrorMessage: String? = null
    private var maxLengthForHintLabel: Int? = null

    init {
        context.obtainStyledAttributes(attrs, R.styleable.EasyPayEditText).apply {
            binding.apply {
                setLabel(getString(R.styleable.EasyPayEditText_title))
                applyUppercaseForLabel(
                    getBoolean(
                        R.styleable.EasyPayEditText_uppercaseLabel,
                        false
                    )
                )
                setMaxLength(getInt(R.styleable.EasyPayEditText_maxLength, MAX_LENGTH_NOT_GIVEN))
                regexToValidate = getString(R.styleable.EasyPayEditText_regex)
                regexErrorMessage = getString(R.styleable.EasyPayEditText_regexErrorMessage)
            }
            recycle()
        }
        listenForTextChanges()
        setImeActionDoneAction()
    }

    //region Open methods

    open fun getText(): String {
        return binding.editText.text.toString()
    }

    open fun setText(text: String) {
        binding.editText.setText(text)
    }

    protected open fun prepareValidationRules() {
        validationRules.clear()
        setRegexValidationRule()
    }

    //endregion

    //region Edit Text modifiers

    fun setOnLostFocusListener(onLostFocusListener: OnLostFocusListener?) {
        this.onLostFocusListener = onLostFocusListener
    }

    protected fun setLabel(label: String?) {
        binding.til.hint = label
    }

    protected fun setNumberOnlyInputType() {
        binding.editText.inputType = InputType.TYPE_CLASS_NUMBER
    }

    protected fun setStartIconDrawable(@DrawableRes drawableRes: Int) {
        binding.til.setStartIconDrawable(drawableRes)
    }

    protected fun setPasswordMode() {
        binding.apply {
            til.apply {
                endIconMode = TextInputLayout.END_ICON_PASSWORD_TOGGLE
                setEndIconDrawable(R.drawable.selector_ic_show_password)
                setEndIconTintList(context.getColorStateList(R.color.easypay_widget_field_hint_foreground))
                addPasswordEndIconClickListener()
            }
            editText.apply {
                transformationMethod = AsteriskPasswordTransformationMethod()
                inputType =
                    InputType.TYPE_CLASS_NUMBER or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            }
        }
    }

    protected fun setMaxLengthFilter(maxLength: Int) {
        if (maxLength <= MAX_LENGTH_NOT_GIVEN) return
        setFilter(InputFilter.LengthFilter(maxLength))
    }

    private fun setMaxLength(maxLength: Int) {
        if (maxLength <= MAX_LENGTH_NOT_GIVEN) return
        maxLengthForHintLabel = maxLength
        setMaxLengthFilter(maxLength)
    }

    protected open fun setInternalOnFocusChangeListener(hasFocus: Boolean) {
        if (hasFocus) {
            applyMaxCharactersInfoLabelIfPossible()
            return
        }
        hideInfoLabel()
        if (getText().isBlank()) {
            hideErrorMessage()
            clearText()
        }
        onLostFocusListener?.onLostFocus()
    }

    //endregion

    //region Setting validation rules

    protected fun addValidationRule(vararg rules: ValidationRule) {
        validationRules.addAll(rules)
    }

    private fun setRegexValidationRule() {
        regexToValidate?.let {
            val rule = ValidationRule(
                isValid = Validation.isRegexValid(getText(), it),
                error = regexErrorMessage ?: context.getString(R.string.invalid_characters)
            )
            addValidationRule(rule)
        }
    }

    protected fun setMinLengthValidationRule(minLength: Int, @StringRes errorMessageId: Int) {
        val rule = ValidationRule(
            isValid = getText().length >= minLength,
            error = context.getString(errorMessageId)
        )
        addValidationRule(rule)
    }

    //endregion

    //region Validation

    fun validate(): ValidationState {
        if (getText().isBlank()) return ValidationState.NotFilled
        prepareValidationRules()
        validationRules.forEach { rule ->
            if (!validateRule(rule)) return ValidationState.InternalValidationInvalid
        }
        return ValidationState.Valid
    }

    private fun validateRule(rule: ValidationRule): Boolean {
        return when {
            !rule.isValid -> {
                isErrorStateDisplayed = true
                displayErrorMessage(rule.error)
                false
            }

            else -> {
                isErrorStateDisplayed = false
                hideErrorMessage()
                true
            }
        }
    }

    //endregion

    //region Helpers

    private fun clearText() {
        binding.editText.setText("")
    }

    private fun applyUppercaseForLabel(shouldApplyUppercase: Boolean) {
        if (shouldApplyUppercase) {
            binding.til.hint = binding.til.hint.toString().uppercase()
        }
    }

    private fun displayErrorMessage(message: String?) {
        binding.apply {
            til.apply {
                setDefaultHintColor(R.color.easypay_widget_text_foreground_error)
                setStartIconTintList(context.getColorStateList(R.color.easypay_widget_text_foreground_error))
                setEndIconTintList(context.getColorStateList(R.color.easypay_widget_text_foreground_error))
            }
            editText.apply {
                setBackgroundResource(R.drawable.selector_bg_edit_text_error)
                setTextColor(context.getColor(R.color.easypay_widget_text_foreground_error))
            }
            tvError.apply {
                text = message
                visibility = VISIBLE
            }
            hideInfoLabel()
        }
    }

    private fun hideErrorMessage() {
        binding.apply {
            til.apply {
                setDefaultHintColor(R.color.easypay_widget_field_hint_foreground)
                setStartIconTintList(context.getColorStateList(R.color.easypay_widget_field_hint_foreground))
                setEndIconTintList(context.getColorStateList(R.color.easypay_widget_field_hint_foreground))
            }
            editText.apply {
                setBackgroundResource(R.drawable.selector_bg_edit_text)
                setTextColor(context.getColor(R.color.easypay_widget_text_foreground))
            }
            tvError.apply {
                text = ""
                visibility = GONE
            }
        }
    }

    /**
     * Hides the keyboard when the user presses the done button on the keyboard in the last field in the form.
     */
    private fun setImeActionDoneAction() {
        binding.editText.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                v.clearFocus()
                v.hideKeyboard()
            }
            return@setOnEditorActionListener false
        }
    }

    private fun setFilter(filter: InputFilter) {
        val filters = binding.editText.filters.toMutableList()
        filters.add(filter)
        binding.editText.filters = filters.toTypedArray()
    }

    private fun listenForTextChanges() {
        binding.editText.apply {
            setOnFocusChangeListener { _, hasFocus ->
                setInternalOnFocusChangeListener(hasFocus)
            }
            doAfterTextChanged { applyMaxCharactersInfoLabelIfPossible() }
        }
    }

    private fun applyMaxCharactersInfoLabelIfPossible() {
        val textLength = getText().length
        maxLengthForHintLabel?.let { maxLength ->
            binding.tvInfo.apply {
                if (maxLength > textLength || isErrorStateDisplayed) {
                    hideInfoLabel()
                } else {
                    showInfoLabel(
                        context.getString(
                            R.string.you_have_reached_characters_limit,
                            maxLength
                        )
                    )
                }
            }
        }
    }

    private fun showInfoLabel(label: String) {
        binding.tvInfo.apply {
            text = label
            visibility = VISIBLE
        }
    }

    private fun hideInfoLabel() {
        binding.tvInfo.apply {
            text = ""
            visibility = GONE
        }
    }

    //endregion

    //region Interfaces

    fun interface OnLostFocusListener {
        fun onLostFocus()
    }

    //endregion

}