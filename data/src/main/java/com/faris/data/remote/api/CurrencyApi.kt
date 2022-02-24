package com.faris.data.remote.api

import com.faris.data.remote.dto.CurrencyDto
import retrofit2.http.*

interface CurrencyApi {

    @GET("symbols")
    suspend fun getCurrencies(@Query("access_key") apiKey: String = API_KEY): CurrencyDto.CurrencyList

    @GET("{date}")
    suspend fun convert(
        @Path("date") date: String,
        @Query("access_key") apiKey: String = API_KEY,
        @Query("base") from: String,
        @Query("symbols") to: String,
    ): CurrencyDto.ConversionResultDto

    companion object {
        private const val API_KEY = "ccad955f906284ea70951f4bc455c478"
    }
}