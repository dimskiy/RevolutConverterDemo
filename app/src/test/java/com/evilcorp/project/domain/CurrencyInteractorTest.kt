package com.evilcorp.project.domain

import com.evilcorp.project.RxTestBase
import com.evilcorp.project.domain.contracts.RatesProvider
import com.evilcorp.project.domain.contracts.WorldDataProvider
import com.evilcorp.project.domain.model.CurrencyInfoModel
import com.evilcorp.project.domain.model.RateModel
import com.evilcorp.project.presentation.model.CurrencyRateModel
import io.reactivex.Observable
import io.reactivex.Single
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.util.concurrent.TimeoutException

private const val CURRENCY_BRL = "BRL"

class CurrencyInteractorTest: RxTestBase() {
    private val ratesProvider = mock(RatesProvider::class.java)
    private val worldProvider = mock(WorldDataProvider::class.java)
    private val instance = CurrencyInteractorImpl(ratesProvider, worldProvider)

    private val ratesToEur = listOf(
        RateModel("AUD", 1.583f),
        RateModel("BGN", 1.975f),
        RateModel("BRL", 4.24f),
        RateModel("CAD", 1.523f),
        RateModel("CHF", 1.138f)
    )

    @Test
    fun `Update rates by BRL rate`() {
        val testList = listOf(
            CurrencyRateModel("AUD", "", 0, 1.59f),
            CurrencyRateModel("BGN", "", 0, 1.98f),
            CurrencyRateModel("CHF", "", 0, 1.14f)
        )
        val referenceList = listOf(
            CurrencyRateModel("AUD", "", 0, 0.3733f),
            CurrencyRateModel("BGN", "", 0, 0.4658f),
            CurrencyRateModel("CHF", "", 0, 0.2684f)
        )

        `when`(ratesProvider.getRates(any()))
            .thenReturn(Single.fromCallable { ratesToEur })

        instance.getUpdatedWithApiRates(
            getBaseRateBy(CURRENCY_BRL),
            testList
        )
            .flatMapObservable { Observable.fromIterable(it) }
            .test()
            .assertValueSequence(referenceList)
    }

    @Test
    fun `Update rates by BRL rate WHEN shuffled order`() {
        val testList = listOf(
            CurrencyRateModel("BGN", "", 0, 1.98f),
            CurrencyRateModel("AUD", "", 0, 1.59f),
            CurrencyRateModel("BRL", "", 0, 4.19f)
        )
        val referenceList = listOf(
            CurrencyRateModel("BGN", "", 0, 0.4658f),
            CurrencyRateModel("AUD", "", 0, 0.3733f),
            CurrencyRateModel("BRL", "", 0, 1.0f)
        )

        `when`(ratesProvider.getRates(any()))
            .thenReturn(Single.fromCallable { ratesToEur })

        instance.getUpdatedWithApiRates(
            getBaseRateBy(CURRENCY_BRL),
            testList
        )
            .flatMapObservable { Observable.fromIterable(it) }
            .test()
            .assertValueSequence(referenceList)
    }

    @Test
    fun `Update rates by BRL rate WHEN missing currency in list`() {
        val testList = listOf(
            CurrencyRateModel("CAD", "", 0, 1.51f),
            CurrencyRateModel("XXX", "", 0, 1.23f)
        )
        val referenceList = listOf(
            CurrencyRateModel("CAD", "", 0, 0.3592f),
            CurrencyRateModel("XXX", "", 0, 0.0f)
        )

        `when`(ratesProvider.getRates(any()))
            .thenReturn(Single.fromCallable { ratesToEur })

        instance.getUpdatedWithApiRates(
            getBaseRateBy(CURRENCY_BRL),
            testList
        )
            .flatMapObservable { Observable.fromIterable(it) }
            .test()
            .assertValueSequence(referenceList)
    }

    @Test
    fun `Update rates by BRL rate WHEN missing base value`() {
        val testList = listOf(
            CurrencyRateModel("CAD", "", 0, 1.51f),
            CurrencyRateModel("BRL", "", 0, 4.19f)
        )
        val referenceList = listOf(
            CurrencyRateModel("CAD", "", 0, 0.0f),
            CurrencyRateModel("BRL", "", 0, 0.0f)
        )

        `when`(ratesProvider.getRates(any()))
            .thenReturn(Single.fromCallable { ratesToEur })

        instance.getUpdatedWithApiRates(
            getBaseRateBy("XXX"),
            testList
        )
            .flatMapObservable { Observable.fromIterable(it) }
            .test()
            .assertValueSequence(referenceList)
    }

    @Test
    fun `Get initial currency list`() {
        val referenceList = listOf(
            CurrencyRateModel("TEST", "", 0, 1.0f),
            CurrencyRateModel("TEST", "", 0, 12.0f)
        )

        `when`(ratesProvider.getRates(any()))
            .thenReturn(Single.fromCallable { listOf(RateModel("CAD", 12.0f)) })

        `when`(worldProvider.getCurrencyInfo(any()))
            .thenReturn(
                Single.fromCallable { CurrencyInfoModel("TEST", "", 0) }
            )

        instance.getInitialCurrencyList()
            .flatMapObservable { Observable.fromIterable(it) }
            .test()
            .assertValueSequence(referenceList)
    }

    @Test
    fun `Pass Error WHEN worldProvider exception`() {
        `when`(ratesProvider.getRates(any()))
            .thenReturn(Single.fromCallable { listOf(RateModel("CAD", 12.0f)) })

        `when`(worldProvider.getCurrencyInfo(any()))
            .thenReturn(Single.error(Exception("Test exception")))

        instance.getInitialCurrencyList()
            .flatMapObservable { Observable.fromIterable(it) }
            .test()
            .assertError(Throwable::class.java)
    }

    @Test
    fun `Pass Error WHEN ratesProvider exception`() {
        `when`(ratesProvider.getRates(any()))
            .thenReturn(Single.error(TimeoutException("Test exception")))

        `when`(worldProvider.getCurrencyInfo(any()))
            .thenReturn(
                Single.fromCallable { CurrencyInfoModel("TEST", "", 0) }
            )

        instance.getInitialCurrencyList()
            .flatMapObservable { Observable.fromIterable(it) }
            .test()
            .assertError(Throwable::class.java)
    }

    private fun getBaseRateBy(code: String) = CurrencyRateModel(
        currencyCode = code,
        currencyDescription = "",
        currencyImageRes = 0,
        value =  1.0f
    )
}