package com.easypaysolutions.api

import android.content.Context
import com.easypaysolutions.networking.NetworkDataSource
import com.easypaysolutions.networking.authentication.AuthHelper
import com.easypaysolutions.networking.authentication.AuthHelperImpl
import com.easypaysolutions.networking.authentication.AuthUtils
import com.easypaysolutions.networking.authentication.AuthUtilsImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit

internal object ApiModule {

    val apiModules = module {
        single<EasyPayService> { provideService(get()) }
        single<EasyPayApiHelper> { provideApiHelper(get(), get(), get()) }
        single { provideAuthHelper(androidContext(), get()) }
        single { provideAuthUtils() }
    }

    private fun provideService(retrofit: Retrofit): EasyPayService =
        retrofit.create(EasyPayService::class.java)

    private fun provideAuthHelper(context: Context, authUtils: AuthUtils): AuthHelper =
        AuthHelperImpl(context, authUtils = authUtils)

    private fun provideAuthUtils(): AuthUtils = AuthUtilsImpl()

    private fun provideApiHelper(
        easyPayService: EasyPayService,
        networkDataSource: NetworkDataSource,
        authHelper: AuthHelper,
    ): EasyPayApiHelper = EasyPayApiHelperImpl(easyPayService, networkDataSource, authHelper)
}
