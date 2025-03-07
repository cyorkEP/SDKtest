package com.easypaysolutions.common.di

import com.easypaysolutions.common.presentation.add_new_card.AddNewCardViewModel
import com.easypaysolutions.repositories.annual_consent.cancel.CancelAnnualConsent
import com.easypaysolutions.repositories.annual_consent.create.CreateAnnualConsent
import com.easypaysolutions.repositories.annual_consent.list.ListAnnualConsents
import com.easypaysolutions.repositories.annual_consent.process_payment.ProcessPaymentAnnual
import com.easypaysolutions.repositories.charge_cc.ChargeCreditCard
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import kotlin.coroutines.CoroutineContext

internal object CommonDiModules {
    val module = module {
        // SDK modules
        single<ListAnnualConsents> { ListAnnualConsents() }
        single<CreateAnnualConsent> { CreateAnnualConsent() }
        single<ChargeCreditCard> { ChargeCreditCard() }
        single<ProcessPaymentAnnual> { ProcessPaymentAnnual() }
        single<CancelAnnualConsent> { CancelAnnualConsent() }

        // Coroutine Context
        single<CoroutineContext> { Dispatchers.IO }

        // ViewModels
        viewModel { AddNewCardViewModel(get()) }
    }
}