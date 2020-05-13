package com.evilcorp.project.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.evilcorp.project.presentation.contracts.CurrencyInteractor
import com.evilcorp.project.presentation.ratesConverter.RatesViewModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RatesViewModelFactory
@Inject constructor(private val currencyInteractor: CurrencyInteractor): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(RatesViewModel::class.java) -> {
                RatesViewModel(
                    currencyInteractor
                ) as T
            }
            else -> throw IllegalStateException("ViewModel not found for '$modelClass'")
        }
    }
}