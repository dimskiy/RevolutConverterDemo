package com.evilcorp.project.di

import com.evilcorp.project.presentation.ratesConverter.RatesFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(ratesFragment: RatesFragment)
}