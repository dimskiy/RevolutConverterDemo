package com.evilcorp.project.domain.contracts

import com.evilcorp.project.domain.model.CurrencyInfoModel
import io.reactivex.Single

interface WorldDataProvider {

    fun getCurrencyInfo(currencyCode: String): Single<CurrencyInfoModel>
}