package com.easypay_sample.utils

import android.app.AlertDialog
import android.content.Context
import android.text.InputType
import android.widget.FrameLayout
import androidx.annotation.StringRes
import com.easypay_sample.R
import com.google.android.material.textfield.TextInputEditText

object AlertUtils {

    fun showAlert(context: Context, message: String?) {
        val alert = AlertDialog.Builder(context)
            .setCancelable(true)
            .setMessage(message)

        alert.show()
    }

    fun showInputAlert(
        context: Context,
        @StringRes title: Int = R.string.app_name,
        @StringRes message: Int = R.string.app_name,
        @StringRes positiveText: Int = R.string.ok,
        @StringRes inputHintText: Int? = null,
        @StringRes negativeText: Int = R.string.cancel,
        inputType: Int = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL,
        onPositive: (String) -> Unit = {},
        onNegative: () -> Unit = {},
    ) {
        val editText = TextInputEditText(context)
        inputHintText?.let {
            editText.setHint(it)
        }
        editText.inputType = inputType
        val alert = AlertDialog.Builder(context)
            .setCancelable(true)
            .setTitle(title)
            .setMessage(message)
            .setEditText(editText)
            .setPositiveButton(positiveText) { dialog, _ ->
                dialog.dismiss()
                onPositive(editText.text.toString())
            }
            .setNegativeButton(negativeText) { dialog, _ ->
                dialog.dismiss()
                onNegative()
            }

        alert.show()
    }
}

private fun AlertDialog.Builder.setEditText(editText: TextInputEditText): AlertDialog.Builder {
    val container = FrameLayout(context)
    container.addView(editText)
    val containerParams = FrameLayout.LayoutParams(
        FrameLayout.LayoutParams.MATCH_PARENT,
        FrameLayout.LayoutParams.WRAP_CONTENT
    )
    val marginHorizontal = 48F
    containerParams.marginStart = marginHorizontal.toInt()
    containerParams.marginEnd = marginHorizontal.toInt()
    container.layoutParams = containerParams

    val superContainer = FrameLayout(context)
    superContainer.addView(container)

    setView(superContainer)

    return this
}