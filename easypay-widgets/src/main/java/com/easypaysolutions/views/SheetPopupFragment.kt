package com.easypaysolutions.views

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.easypaysolutions.utils.extensions.hideSheetPopup
import com.easypaysolutions.widgets.databinding.FragmentSheetPopupBinding
import kotlinx.parcelize.Parcelize

@Parcelize
internal data class SheetPopupInput(
    @StringRes val titleId: Int,
    @StringRes val descriptionId: Int,
    @StringRes val secondaryButtonLabelId: Int,
    @StringRes val primaryButtonLabelId: Int,
    val secondaryAction: (() -> Unit)? = null,
    val primaryAction: () -> Unit,
) : Parcelable

internal class SheetPopupFragment private constructor() : Fragment() {

    companion object {
        const val TAG = "SheetPopupFragment"
        const val INPUT_TAG = "SheetPopupInput"

        fun newInstance(input: SheetPopupInput): SheetPopupFragment {
            return SheetPopupFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(INPUT_TAG, input)
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    private val input: SheetPopupInput by lazy {
        arguments?.getParcelable<SheetPopupInput>(INPUT_TAG)
            ?: throw IllegalArgumentException("SheetPopupFragment called without input provided")
    }

    private lateinit var binding: FragmentSheetPopupBinding

    //region Lifecycle

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSheetPopupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initComponents()
    }

    //endregion

    //region Components initialization

    private fun initComponents() {
        binding.apply {
            background.setOnClickListener { closeAction() }
            tvTitle.setText(input.titleId)
            tvConfirmation.setText(input.descriptionId)
            btnSecondary.apply {
                setText(input.secondaryButtonLabelId)
                setOnClickListener { onSecondaryButtonClicked() }
            }
            btnPrimary.apply {
                setText(input.primaryButtonLabelId)
                setOnClickListener { onPrimaryButtonClicked() }
            }
        }
    }

    //endregion

    //region Actions

    private fun closeAction() {
        input.secondaryAction?.invoke()
        hideSheetPopup()
    }

    private fun onSecondaryButtonClicked() {
        closeAction()
    }

    private fun onPrimaryButtonClicked() {
        input.primaryAction()
        hideSheetPopup()
    }

    //endregion

}