package com.evilcorp.project.domain

import com.evilcorp.project.domain.contracts.RatesProvider
import com.evilcorp.project.domain.contracts.WorldDataProvider
import com.evilcorp.project.domain.model.RateModel
import com.evilcorp.project.helpers.mapToCurrencyRate
import com.evilcorp.project.presentation.contracts.CurrencyInteractor
import com.evilcorp.project.presentation.model.CurrencyRateModel
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject

const val DEFAULT_BASE_CURRENCY = "EUR"

open class CurrencyInteractorImpl(
    private val ratesProvider: RatesProvider,
    private val worldDataProvider: WorldDataProvider
) : CurrencyInteractor {

    private val apiCache = BehaviorSubject.create<List<RateModel>>()

    override fun getUpdatedWithApiRates(
        selected: CurrencyRateModel,
        list: List<CurrencyRateModel>
    ): Single<List<CurrencyRateModel>> = getAllRates()
        .doOnSuccess(apiCache::onNext)
        .onErrorResumeNext(apiCache.firstOrError())
        .map { updateRates(it, list, selected) }

    private fun updateRates(
        rates: List<RateModel>,
        currencies: List<CurrencyRateModel>,
        baseRate: CurrencyRateModel
    ): List<CurrencyRateModel> {

        val calc = Calculator(rates, baseRate)
        return currencies.map { oldRate ->
            oldRate.copy(value = calc.calculateRate(oldRate.currencyCode))
        }
    }

    override fun getInitialCurrencyList(): Single<List<CurrencyRateModel>> {
        return getAllRates()
            .flatMapObservable { Observable.fromIterable(it) }
            .concatMapSingle { rateModel ->
                worldDataProvider.getCurrencyInfo(rateModel.code)
                    .map { it.mapToCurrencyRate(rateModel.rate) }
            }
            .toList()
    }

    private fun getAllRates(): Single<List<RateModel>> {
        return ratesProvider.getRates(DEFAULT_BASE_CURRENCY)
            .flatMapObservable { Observable.fromIterable(it) }
            .startWith(RateModel(DEFAULT_BASE_CURRENCY, 1f))
            .toList()
    }
}