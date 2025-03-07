package com.easypaysolutions.common.presentation.add_new_card

import com.easypaysolutions.utils.secured.SecureData

internal data class AddNewCardViewData(
    var last4digits: String,
    var encryptedCardNumber: SecureData<String>,
    var cardHolderName: String,
    var expMonth: String,
    var expYear: String,
    var cvv: String,
    var streetAddress: String,
    var zip: String,

    var shouldSaveCard: Boolean,
)