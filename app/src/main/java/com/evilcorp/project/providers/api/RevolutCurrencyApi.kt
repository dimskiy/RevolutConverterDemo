package com.evilcorp.project.providers.api

import com.google.gson.annotations.SerializedName
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

data class ApiRatesResponse(
    @SerializedName("baseCurrency") val baseCurrency: String?,
    @SerializedName("rates") val rates: Map<String, Double>
)

interface RevolutCurrencyApi {

    @GET("latest")
    fun getRatesResponse(@Query("base")baseCurrency: String): Single<ApiRatesResponse>
}