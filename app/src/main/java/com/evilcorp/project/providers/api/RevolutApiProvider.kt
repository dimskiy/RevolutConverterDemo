package com.evilcorp.project.providers.api

import com.evilcorp.project.domain.contracts.RatesProvider
import com.evilcorp.project.domain.model.RateModel
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

fun ApiRatesResponse.mapToRateModels(): List<RateModel> {
    return rates.map { RateModel(it.key, it.value.toFloat()) }
}

private const val BASE_URL = "https://hiring.revolut.codes/api/android/"

class RevolutApiProvider(okhttp: OkHttpClient) : RatesProvider {

    private val service: RevolutCurrencyApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okhttp)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(RevolutCurrencyApi::class.java)

    override fun getRates(baseCurrencyCode: String): Single<List<RateModel>> {
        return service.getRatesResponse(baseCurrencyCode)
            .subscribeOn(Schedulers.io())
            .map(ApiRatesResponse::mapToRateModels)
    }
}