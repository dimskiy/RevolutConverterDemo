package com.evilcorp.project.helpers

import android.content.Context
import com.evilcorp.project.App
import com.evilcorp.project.domain.model.CurrencyInfoModel
import com.evilcorp.project.presentation.model.CurrencyRateModel
import com.evilcorp.project.presentation.ratesConverter.RatesFragment

fun Context.injectRatesFragment(fragment: RatesFragment) {
    (applicationContext as? App)?.appComponent?.inject(fragment)
        ?: throw IllegalStateException("Cant access appComponent in 'App' class")
}

fun CurrencyInfoModel.mapToCurrencyRate(rateValue: Float): CurrencyRateModel {
    return CurrencyRateModel(
        code,
        description,
        imageRes,
        rateValue
    )
}