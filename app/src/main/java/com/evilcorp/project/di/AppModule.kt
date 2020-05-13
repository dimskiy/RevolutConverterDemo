package com.evilcorp.project.di

import android.app.Application
import com.evilcorp.project.domain.CurrencyInteractorImpl
import com.evilcorp.project.domain.contracts.RatesProvider
import com.evilcorp.project.domain.contracts.WorldDataProvider
import com.evilcorp.project.presentation.contracts.CurrencyInteractor
import com.evilcorp.project.providers.api.RevolutApiProvider
import com.evilcorp.project.providers.worldResources.WorldCountryFlagProvider
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Module
class AppModule(private val app: Application) {

    @Singleton
    @Provides
    fun provideOkhttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addNetworkInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    @Singleton
    @Provides
    fun provideRatesProvider(okhttpClient: OkHttpClient): RatesProvider = RevolutApiProvider(okhttpClient)

    @Singleton
    @Provides
    fun provideWorldProvider(): WorldDataProvider = WorldCountryFlagProvider(app)

    @Singleton
    @Provides
    fun provideCurrencyInteractor(
        ratesProvider: RatesProvider,
        worldDataProvider: WorldDataProvider
    ): CurrencyInteractor = CurrencyInteractorImpl(ratesProvider, worldDataProvider)
}