package com.easypay_sample.di

import com.easypaysolutions.repositories.annual_consent.cancel.CancelAnnualConsent
import com.easypaysolutions.repositories.annual_consent.create.CreateAnnualConsent
import com.easypaysolutions.repositories.annual_consent.list.ListAnnualConsents
import com.easypaysolutions.repositories.annual_consent.process_payment.ProcessPaymentAnnual
import com.easypaysolutions.repositories.charge_cc.ChargeCreditCard
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideListAnnualConsents() = ListAnnualConsents()

    @Singleton
    @Provides
    fun provideCancelAnnualConsent() = CancelAnnualConsent()

    @Singleton
    @Provides
    fun provideChargeCreditCard() = ChargeCreditCard()

    @Singleton
    @Provides
    fun provideProcessPaymentAnnual() = ProcessPaymentAnnual()

    @Singleton
    @Provides
    fun provideCreateAnnualConsent() = CreateAnnualConsent()
}