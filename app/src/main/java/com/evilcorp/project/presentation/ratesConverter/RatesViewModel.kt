package com.evilcorp.project.presentation.ratesConverter

import androidx.lifecycle.ViewModel
import com.evilcorp.project.domain.DEFAULT_BASE_CURRENCY
import com.evilcorp.project.presentation.contracts.CurrencyInteractor
import com.evilcorp.project.presentation.model.CurrencyRateModel
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit

private const val RATES_PEEK_INTERVAL_SEC = 1L

class RatesViewModel(private val interactor: CurrencyInteractor) : ViewModel() {

    private val currencyList = BehaviorSubject.create<List<CurrencyRateModel>>()
    private val selectedCurrency = BehaviorSubject.createDefault(
        CurrencyRateModel(
            DEFAULT_BASE_CURRENCY,
            "",
            0,
            1f
        )
    )
    private val selectedValue = BehaviorSubject.createDefault(1f)

    fun onCurrencyUpdate(currency: CurrencyRateModel) {
        selectedCurrency.onNext(currency)
    }

    fun onSelectedValueUpdate(value: Float) {
        selectedValue.onNext(value)
    }

    fun getCurrencyList(): Observable<List<CurrencyRateModel>> {
        return Observable.combineLatest<Long, CurrencyRateModel, CurrencyRateModel>(
            Observable.interval(RATES_PEEK_INTERVAL_SEC, TimeUnit.SECONDS),
            currencyAndValueCombined(),
            BiFunction { _, selectedItem -> selectedItem }
        )
            .concatMapSingle { selected ->
                getInitCurrencyList()
                    .firstOrError()
                    .map { sortListBySelection(selected, it) }
                    .doOnEvent { list, _ -> currencyList.onNext(list) }
                    .flatMap { interactor.getUpdatedWithApiRates(selected, it) }
            }
    }

    private fun getInitCurrencyList(): Observable<List<CurrencyRateModel>> {
        return (currencyList.takeIf { it.hasValue() } ?: interactor.getInitialCurrencyList()
            .toObservable()
            .doOnNext(currencyList::onNext))
    }

    private fun currencyAndValueCombined(): Observable<CurrencyRateModel> {
        return Observable.combineLatest(
            selectedCurrency,
            selectedValue.debounce(300, TimeUnit.MILLISECONDS),
            BiFunction { currencyRate, newValue -> currencyRate.copy(value = newValue) }
        )
    }

    private fun sortListBySelection(
        selection: CurrencyRateModel,
        data: List<CurrencyRateModel>
    ): List<CurrencyRateModel> {
        return if (data.firstOrNull()?.currencyCode == selection.currencyCode) {
            data
        } else {
            val others = data.filter { it.currencyCode != selection.currencyCode }
            return listOf(selection).plus(others)
        }
    }
}