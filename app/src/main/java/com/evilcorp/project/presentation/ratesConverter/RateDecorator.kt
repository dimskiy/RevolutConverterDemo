package com.evilcorp.project.presentation.ratesConverter

import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.pow
import kotlin.math.roundToInt

private const val DECIMAL_NUMBER = 2

@Singleton
class RateDecorator
@Inject constructor() {
    private val multiplier = 10.0.pow(DECIMAL_NUMBER.toDouble())

    fun getRateDecorated(value: Float): String {
        val valueRounded = (value * multiplier).roundToInt() / multiplier
        val hasDecimal = valueRounded % 1 > 0
        return when {
            valueRounded == 0.0 -> ""
            hasDecimal -> valueRounded.toString()
            else -> valueRounded.toInt().toString()
        }
    }
}