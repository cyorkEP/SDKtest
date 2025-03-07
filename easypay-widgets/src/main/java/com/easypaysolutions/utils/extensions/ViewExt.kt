package com.easypaysolutions.utils.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.text.method.HideReturnsTransformationMethod
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.easypaysolutions.utils.AsteriskPasswordTransformationMethod
import com.easypaysolutions.views.SheetPopupFragment
import com.easypaysolutions.views.SheetPopupInput
import com.easypaysolutions.widgets.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

internal fun TextInputLayout.setDefaultHintColor(@ColorRes color: Int) {
    defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(context, color))
}

internal fun TextInputLayout.addPasswordEndIconClickListener() {
    var isPasswordVisible = false
    setEndIconOnClickListener {
        editText?.let {
            if (isPasswordVisible) {
                isPasswordVisible = false
                it.transformationMethod = AsteriskPasswordTransformationMethod()
            } else {
                isPasswordVisible = true
                it.transformationMethod = HideReturnsTransformationMethod()
            }
            it.setSelection(it.length())
        }
    }
}

@SuppressLint("SetTextI18n")
internal fun TextInputEditText.showOnlyLastDigits(numberOfVisibleLastDigits: Int) {
    val text = text.toString().replace(" ","")
    val visibleText = text.takeLast(numberOfVisibleLastDigits)
    val hiddenText = text.dropLast(numberOfVisibleLastDigits).replace("[0-9]".toRegex(), "*")
    val newText = "$hiddenText$visibleText".chunked(4).joinToString(" ")
    setText(newText)
}

internal fun TextView.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    imm?.hideSoftInputFromWindow(windowToken, 0)
}

internal fun View?.findSuitableParent(): ViewGroup? {
    var view = this
    var fallback: ViewGroup? = null
    do {
        if (view is CoordinatorLayout) {
            return view
        } else if (view is FrameLayout) {
            if (view.id == android.R.id.content) {
                return view
            } else {
                fallback = view
            }
        }

        if (view != null) {
            val parent = view.parent
            view = if (parent is View) parent else null
        }
    } while (view != null)

    return fallback
}

internal fun Fragment.showSheetPopup(sheetPopupInput: SheetPopupInput) {
    val fragment = SheetPopupFragment.newInstance(sheetPopupInput)
    val transaction = requireActivity().supportFragmentManager.beginTransaction()
    transaction.add(R.id.fragment_container_view, fragment, SheetPopupFragment.TAG)
    transaction.commit()
}

internal fun Fragment.hideSheetPopup() {
    requireActivity().supportFragmentManager.findFragmentByTag(SheetPopupFragment.TAG)?.let {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.remove(it)
        transaction.commit()
    }
}

internal fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

private fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

internal fun LottieAnimationView.show() {
    visibility = View.VISIBLE
}

internal fun LottieAnimationView.hide() {
    visibility = View.GONE
}