package com.faris.data.remote.api

import com.faris.data.remote.dto.CurrencyDto
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyApi {

    @GET("symbols")
    suspend fun getCurrencies(@Query("access_key") apiKey: String = API_KEY) : CurrencyDto.CurrencyList

    companion object {
        private const val API_KEY = "dd08bd55bf9cebd6d739ab85a3e88426"
    }
}