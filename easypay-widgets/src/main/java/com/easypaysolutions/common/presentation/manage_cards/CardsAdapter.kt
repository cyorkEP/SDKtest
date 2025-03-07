package com.easypaysolutions.common.presentation.manage_cards

import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.easypaysolutions.api.responses.annual_consent.AnnualConsent
import com.easypaysolutions.widgets.R
import com.easypaysolutions.widgets.databinding.ItemPaymentMethodCardBinding

internal class CardsAdapter(
    private val listener: Listener,
    initiallySelectedCardId: Int? = null,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val cards = mutableListOf<AnnualConsent>()
    private var selectedPaymentMethodId: Int? = initiallySelectedCardId
    val selectedCard: AnnualConsent?
        get() {
            return selectedPaymentMethodId?.let { selectedCardId ->
                cards.firstOrNull { it.id == selectedCardId }
            }
        }

    //region Overridden

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemPaymentMethodCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return when (ItemViewType.entries[viewType]) {
            ItemViewType.SAVED_CARD -> createSavedCardViewHolder(binding)
            ItemViewType.ADD_NEW_CARD -> createAddNewCardViewHolder(binding)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            ItemViewType.ADD_NEW_CARD.ordinal
        } else {
            ItemViewType.SAVED_CARD.ordinal
        }
    }

    override fun getItemCount(): Int {
        // Include the "Add new card" item
        return cards.size + 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SavedCardViewHolder -> {
                holder.bind(cards[position - 1])
            }

            is AddNewCardViewHolder -> {
                holder.bind()
            }
        }
    }

    //endregion

    //region Public

    fun setCards(cards: List<AnnualConsent>) {
        this.cards.clear()
        this.cards.addAll(cards)
        notifyDataSetChanged()
    }

    //endregion

    //region Helpers

    private fun updateSelectedPaymentMethod(selectedCard: AnnualConsent) {
        val currentlySelectedPosition = cards.indexOfFirst {
            it.id == selectedPaymentMethodId
        }
        val newSelectedPosition = cards.indexOfFirst {
            it.id == selectedCard.id
        }
        if (currentlySelectedPosition != newSelectedPosition) {
            // selected a new Card
            notifyItemChanged(currentlySelectedPosition + 1)
            selectedPaymentMethodId = cards.getOrNull(newSelectedPosition)?.id
        }

        // Notify the current position even if it's the currently selected position so that the
        // ItemAnimator defined in PaymentMethodActivity is triggered.
        notifyItemChanged(newSelectedPosition + 1)
    }

    //endregion

    //region ViewHolders

    private fun createAddNewCardViewHolder(binding: ItemPaymentMethodCardBinding): AddNewCardViewHolder {
        return AddNewCardViewHolder(binding)
    }

    private fun createSavedCardViewHolder(binding: ItemPaymentMethodCardBinding): SavedCardViewHolder {
        return SavedCardViewHolder(binding)
    }

    inner class SavedCardViewHolder(
        private val binding: ItemPaymentMethodCardBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(annualConsent: AnnualConsent) {
            binding.apply {
                tvLabel.text = binding.root.context.getString(
                    R.string.hidden_card_format,
                    annualConsent.accountNumber
                )

                if (annualConsent.id == selectedPaymentMethodId) {
                    cardView.setBackgroundResource(R.drawable.bg_payment_method_card_selected)
                    tvLabel.setTextColor(binding.root.context.getColor(R.color.easypay_widget_payment_method_item_text_selected))
                    ivTopRight.visibility = VISIBLE
                } else {
                    cardView.setBackgroundResource(R.drawable.bg_payment_method_card)
                    tvLabel.setTextColor(binding.root.context.getColor(R.color.easypay_widget_payment_method_item_text))
                    ivTopRight.visibility = GONE
                }

                btnAction.setOnClickListener {
                    updateSelectedPaymentMethod(annualConsent)
                    listener.onSelectCardAction(selectedCard)
                }
            }
        }
    }

    inner class AddNewCardViewHolder(
        private val binding: ItemPaymentMethodCardBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.apply {
                ivTopLeft.setImageResource(R.drawable.ic_plus_circle_filled)
                ivTopRight.visibility = GONE
                tvLabel.text = binding.root.context.getString(R.string.create_new_card)
                btnAction.setOnClickListener {
                    listener.onAddCardClick()
                }
            }
        }
    }

    //endregion

    //region Enums & Interfaces

    internal interface Listener {
        fun onAddCardClick()
        fun onSelectCardAction(annualConsent: AnnualConsent?)
    }

    enum class ItemViewType {
        SAVED_CARD,
        ADD_NEW_CARD
    }

    //endregion

}