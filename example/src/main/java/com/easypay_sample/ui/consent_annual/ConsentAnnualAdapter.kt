package com.easypay_sample.ui.consent_annual

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.easypaysolutions.api.responses.annual_consent.AnnualConsent
import com.easypay_sample.databinding.ItemAnnualConsentBinding

class ConsentAnnualAdapter(
    private val onDeleteClickedCallback: (AnnualConsent) -> Unit,
    private val onChargeClickedCallback: (AnnualConsent) -> Unit,
) : ListAdapter<AnnualConsent, ConsentAnnualAdapter.ViewHolder>(DiffCallback) {

    //region Overridden

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemAnnualConsentBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindTo(getItem(position))
    }

    //endregion

    //region Helpers

    private object DiffCallback : DiffUtil.ItemCallback<AnnualConsent>() {
        override fun areItemsTheSame(
            oldItem: AnnualConsent,
            newItem: AnnualConsent,
        ) = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: AnnualConsent,
            newItem: AnnualConsent,
        ) = oldItem.id == newItem.id
    }

    inner class ViewHolder(private val binding: ItemAnnualConsentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindTo(item: AnnualConsent) {
            binding.apply {
                data = item
                btnDelete.setOnClickListener { onDeleteClickedCallback(item) }
                btnCharge.setOnClickListener { onChargeClickedCallback(item) }
            }
        }
    }

    //endregion

}