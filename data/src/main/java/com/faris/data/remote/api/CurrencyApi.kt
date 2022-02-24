package com.faris.data.remote.api

import com.faris.data.remote.dto.CurrencyDto
import retrofit2.http.*

interface CurrencyApi {

    @GET("symbols")
    suspend fun getCurrencies(): CurrencyDto.CurrencyList

    @GET("{date}")
    suspend fun convert(
        @Path("date") date: String,
        @Query("base") from: String,
        @Query("symbols") to: String,
    ): CurrencyDto.ConversionResultDto
}