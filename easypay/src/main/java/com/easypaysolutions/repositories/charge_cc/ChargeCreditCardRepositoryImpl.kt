package com.easypaysolutions.repositories.charge_cc

import com.easypaysolutions.api.EasyPayApiHelper
import com.easypaysolutions.api.requests.charge_cc.ChargeCreditCardRequest
import com.easypaysolutions.api.responses.charge_cc.ChargeCreditCardResult
import com.easypaysolutions.networking.NetworkResource
import com.easypaysolutions.utils.validation.ValidatorUtils

internal class ChargeCreditCardRepositoryImpl(
    private val apiHelper: EasyPayApiHelper,
) : ChargeCreditCardRepository {

    override suspend fun chargeCreditCard(
        params: ChargeCreditCardBodyParams,
    ): NetworkResource<ChargeCreditCardResult> = ValidatorUtils.validate(params) {
        val request = ChargeCreditCardRequest(
            userDataPresent = true,
            body = params.toDto(),
        )
        apiHelper.chargeCreditCard(request)
    }
}