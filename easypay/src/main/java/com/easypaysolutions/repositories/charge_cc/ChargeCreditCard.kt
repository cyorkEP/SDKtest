package com.easypaysolutions.repositories.charge_cc

import com.easypaysolutions.api.responses.charge_cc.ChargeCreditCardResult
import com.easypaysolutions.networking.NetworkResource
import org.koin.java.KoinJavaComponent

class ChargeCreditCard {

    private val chargeCreditCardRepository: ChargeCreditCardRepository by KoinJavaComponent.inject(
        ChargeCreditCardRepository::class.java
    )

    suspend fun chargeCreditCard(
        params: ChargeCreditCardBodyParams,
    ): NetworkResource<ChargeCreditCardResult> {
        return chargeCreditCardRepository.chargeCreditCard(params)
    }
}