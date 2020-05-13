package com.evilcorp.project.domain.contracts

import com.evilcorp.project.domain.model.RateModel
import io.reactivex.Single

interface RatesProvider {

    fun getRates(baseCurrencyCode: String): Single<List<RateModel>>

}