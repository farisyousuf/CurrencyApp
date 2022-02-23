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
//        private const val API_KEY = "dd08bd55bf9cebd6d739ab85a3e88426"
        private const val API_KEY = "bbfdbe4e429a4e816b2aba571d4188f1"
    }
}