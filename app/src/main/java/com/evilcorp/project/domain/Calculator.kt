package com.evilcorp.project.domain

import com.evilcorp.project.domain.model.RateModel
import com.evilcorp.project.presentation.model.CurrencyRateModel
import timber.log.Timber
import kotlin.math.roundToInt

private const val VALUE_DECIMAL_ROUND = 10000
private const val DEFAULT_VALUE = 0f

class Calculator(rates: List<RateModel>, private val inputCurrency: CurrencyRateModel) {
    private val ratesToBaseCurrency: Map<String, Float> = rates.associate {
        it.code to it.rate
    }

    private val baseRate = ratesToBaseCurrency[inputCurrency.currencyCode]

    fun calculateRate(targetCurrency: String): Float {
        val targetRateToBase = ratesToBaseCurrency[targetCurrency]
        val result = if (baseRate != null && targetRateToBase != null) {
            targetRateToBase / baseRate * inputCurrency.value
        } else {
            Timber.w("Currency pair error. Base: '${inputCurrency.currencyCode}', Target: '$targetCurrency'")
            DEFAULT_VALUE
        }
        return (result * VALUE_DECIMAL_ROUND).roundToInt() / VALUE_DECIMAL_ROUND.toFloat()
    }
}