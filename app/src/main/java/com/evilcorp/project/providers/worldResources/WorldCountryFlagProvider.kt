package com.evilcorp.project.providers.worldResources

import android.content.Context
import com.blongho.country_data.Currency
import com.blongho.country_data.World
import com.evilcorp.project.domain.contracts.WorldDataProvider
import com.evilcorp.project.domain.model.CurrencyInfoModel
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class WorldCountryFlagProvider(appContext: Context): WorldDataProvider {

    private val currencyCodeMap: Map<String, Currency>

    init {
        World.init(appContext)
        currencyCodeMap = World.getAllCurrencies()
            .associateBy(Currency::getCode)
    }

    override fun getCurrencyInfo(currencyCode: String): Single<CurrencyInfoModel> {
        return Single.fromCallable {
            currencyCodeMap[currencyCode]?.let { currency ->
                val flagRes = World.getFlagOf(currency.country)
                CurrencyInfoModel(
                    currency.code,
                    currency.name,
                    flagRes
                )
            } ?: throw IllegalStateException("No data for '$currencyCode'")
        }
            .subscribeOn(Schedulers.io())
    }
}