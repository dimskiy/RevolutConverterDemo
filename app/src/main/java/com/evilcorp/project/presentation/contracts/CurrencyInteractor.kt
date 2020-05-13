package com.evilcorp.project.presentation.contracts

import com.evilcorp.project.presentation.model.CurrencyRateModel
import io.reactivex.Observable
import io.reactivex.Single

interface CurrencyInteractor {

    fun getInitialCurrencyList(): Single<List<CurrencyRateModel>>

    fun getUpdatedWithApiRates(
        selected: CurrencyRateModel,
        list: List<CurrencyRateModel>
    ): Single<List<CurrencyRateModel>>
}