package com.easypaysolutions.views

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.easypaysolutions.utils.extensions.findSuitableParent
import com.easypaysolutions.widgets.R
import com.easypaysolutions.widgets.databinding.LayoutSnackbarBinding
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.ContentViewCallback

internal class EasyPaySnackbar(
    parent: ViewGroup,
    content: EasyPaySnackbarView,
) : BaseTransientBottomBar<EasyPaySnackbar>(parent, content, content) {

    init {
        getView().apply {
            setBackgroundColor(ContextCompat.getColor(view.context, android.R.color.transparent))
            setPadding(0, 0, 0, 0)
        }
    }

    companion object {

        //region Public

        fun makeFailure(
            view: View,
            @StringRes messageResId: Int,
        ): EasyPaySnackbar = make(
            view,
            messageResId,
            R.drawable.ic_x_circle_filled_red,
            R.drawable.bg_snackbar_failure,
        )

        fun makeSuccess(
            view: View,
            @StringRes messageResId: Int,
        ): EasyPaySnackbar = make(
            view,
            messageResId,
            R.drawable.ic_check_circle_filled_green,
            R.drawable.bg_snackbar_success
        )

        //endregion

        //region Helpers

        private fun make(
            view: View,
            @StringRes messageResId: Int,
            @DrawableRes iconResId: Int,
            @DrawableRes backgroundResId: Int,
        ): EasyPaySnackbar {
            val context = view.context
            val content = EasyPaySnackbarView(context)
            content.setup(messageResId, iconResId, backgroundResId)
            val parent = view.findSuitableParent() ?: throw IllegalArgumentException(
                "No suitable parent found from the given view. Please provide a valid view."
            )
            return EasyPaySnackbar(parent, content)
        }

        //endregion

    }
}

internal class EasyPaySnackbarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : ConstraintLayout(context, attrs, defStyleAttr), ContentViewCallback {

    companion object {
        private const val DURATION: Long = 500
    }

    private val binding: LayoutSnackbarBinding = LayoutSnackbarBinding.inflate(
        LayoutInflater.from(context), this, true
    )

    //region Overridden

    override fun animateContentIn(delay: Int, duration: Int) {
        val scaleX = ObjectAnimator.ofFloat(binding.root, View.SCALE_X, 0f, 1f)
        val scaleY = ObjectAnimator.ofFloat(binding.root, View.SCALE_Y, 0f, 1f)
        val animatorSet = AnimatorSet().apply {
            interpolator = OvershootInterpolator()
            setDuration(DURATION)
            playTogether(scaleX, scaleY)
        }
        animatorSet.start()
    }

    override fun animateContentOut(delay: Int, duration: Int) {
        // Do nothing
    }

    //endregion

    //region Public

    fun setup(
        @StringRes messageResId: Int,
        @DrawableRes iconResId: Int,
        @DrawableRes backgroundResId: Int,
    ) {
        binding.apply {
            setBackgroundResource(backgroundResId)
            tvSnackbarTitle.apply {
                setText(messageResId)
                setCompoundDrawablesWithIntrinsicBounds(iconResId, 0, 0, 0)
            }
        }
    }

    //endregion

}