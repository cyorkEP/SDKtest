package com.easypaysolutions.networking

import android.content.Context
import com.easypaysolutions.BuildConfig
import com.easypaysolutions.EasyPayConfiguration
import com.easypaysolutions.networking.interceptors.NetworkInterceptor
import com.easypaysolutions.networking.rsa.RsaCertificateManager
import com.easypaysolutions.networking.rsa.RsaCertificateManagerImpl
import com.easypaysolutions.networking.rsa.RsaHelper
import com.easypaysolutions.networking.rsa.RsaHelperImpl
import com.easypaysolutions.utils.DownloadManager
import com.easypaysolutions.utils.DownloadManagerImpl
import com.easypaysolutions.utils.SdkPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit

internal object NetworkingModule {

    val networkingModules = module {
        single { provideGson() }
        single { provideCertificatePinner() }
        single { provideApiClient(get(), get()) }
        single { provideRetrofit(get(), get(), get()) }
        single { provideNetworkDataSource() }
        single { provideNetworkInterceptor() }
        single { provideApiUrl() }
        single { provideDownloadManager() }
        single { provideRsaHelper(get()) }
        single { provideRsaCertificateManager(androidContext(), get(), get()) }
        single { provideSdkPreferences(androidContext()) }
    }

    private fun provideGson(): Gson = GsonBuilder().disableHtmlEscaping().create()

    // TODO: Implement proper certificate pinning
    private fun provideCertificatePinner(): CertificatePinner = CertificatePinner
        .Builder()
        .build()

    private fun provideApiClient(
        certificatePinner: CertificatePinner,
        networkInterceptor: NetworkInterceptor,
    ): OkHttpClient =
        OkHttpClient()
            .newBuilder()
            .addNetworkInterceptor(networkInterceptor)
            // TODO: Uncomment the following line to enable certificate pinning
//        .certificatePinner(certificatePinner)
            .build()

    private fun provideApiUrl(): String =
        BuildConfig.API_URL + EasyPayConfiguration.API_VERSION + "/"

    private fun provideRetrofit(apiClient: OkHttpClient, gson: Gson, apiUrl: String): Retrofit =
        Retrofit.Builder()
            .baseUrl(apiUrl)
            .client(apiClient)
            .addConverterFactory(CustomGsonConverterFactory(gson))
            .build()

    private fun provideNetworkDataSource(): NetworkDataSource = DefaultNetworkDataSource()

    private fun provideNetworkInterceptor() = NetworkInterceptor()

    private fun provideDownloadManager(): DownloadManager =
        DownloadManagerImpl()

    private fun provideRsaHelper(rsaCertificateManager: RsaCertificateManager): RsaHelper =
        RsaHelperImpl(rsaCertificateManager)

    private fun provideRsaCertificateManager(
        context: Context,
        downloadManager: DownloadManager,
        sdkPreferences: SdkPreferences,
    ): RsaCertificateManager = RsaCertificateManagerImpl(context, downloadManager, sdkPreferences)

    private fun provideSdkPreferences(context: Context): SdkPreferences = SdkPreferences(context)
}