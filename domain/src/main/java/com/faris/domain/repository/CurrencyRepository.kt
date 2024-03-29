package com.faris.domain.repository

import com.faris.domain.common.ResultState
import com.faris.domain.entity.response.currency.CurrencyEntity
import kotlinx.coroutines.flow.Flow

interface CurrencyRepository {
    fun getSupportedCurrencies() : Flow<ResultState<CurrencyEntity.CurrencyList>>

    fun getCurrencyConversion(fromCurrency: String, toCurrency: List<String>) : Flow<ResultState<CurrencyEntity.ConversionResult>>

    fun getCurrencyConversionByDays(
        days: Int,
        fromCurrency: String,
        toCurrency: List<String>
    ): Flow<ResultState<List<CurrencyEntity.ConversionResult>>>
}