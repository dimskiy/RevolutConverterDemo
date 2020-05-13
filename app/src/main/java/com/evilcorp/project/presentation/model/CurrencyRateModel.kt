package com.evilcorp.project.presentation.model

data class CurrencyRateModel(
    val currencyCode: String,
    val currencyDescription: String,
    val currencyImageRes: Int,
    val value: Float
)