package com.easypaysolutions.rules.koin

import android.content.Context
import com.easypaysolutions.networking.NetworkingModule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.mockito.Mockito.mock

@ExperimentalCoroutinesApi
internal class KoinNetworkingModulesRule : TestWatcher() {
    override fun starting(description: Description) {
        super.starting(description)
        if (GlobalContext.getOrNull() == null) {
            startKoin {
                androidContext(mock(Context::class.java))
                modules(NetworkingModule.networkingModules)
            }
        }
    }

    override fun finished(description: Description) {
        super.finished(description)
        stopKoin()
    }
}