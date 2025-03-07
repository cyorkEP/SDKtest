package com.easypay_sample.utils

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.easypay_sample.R
import com.easypay_sample.databinding.LayoutEditTextBinding

class AppEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding: LayoutEditTextBinding = LayoutEditTextBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    init {
        if (attrs != null) {
            context.obtainStyledAttributes(attrs, R.styleable.AppEditText).apply {
                binding.apply {
                    val title = getString(R.styleable.AppEditText_title)
                    tvTitle.text = title
                }
                recycle()
            }
        }
    }

    fun getMainText(): String {
        return binding.etInput.text.toString()
    }

    fun setMainText(text: String?) {
        binding.etInput.setText(text)
    }

    companion object Adapter {
        @BindingAdapter("mainText")
        @JvmStatic
        fun setMainText(view: AppEditText, newValue: String?) {
            // Important to break potential infinite loops.
            if (view.getMainText() != newValue) {
                view.setMainText(newValue)
            }
        }

        @InverseBindingAdapter(attribute = "mainText")
        @JvmStatic
        fun getMainText(view: AppEditText): String {
            return view.getMainText()
        }

        @BindingAdapter("app:mainTextAttrChanged")
        @JvmStatic
        fun setListeners(
            view: AppEditText,
            attrChange: InverseBindingListener,
        ) {
            view.binding.etInput.addTextChangedListener {
                attrChange.onChange()
            }
        }

        @BindingAdapter("inputType")
        @JvmStatic
        fun setInputType(view: AppEditText, inputType: Int) {
            view.binding.etInput.inputType = inputType
        }
    }
}