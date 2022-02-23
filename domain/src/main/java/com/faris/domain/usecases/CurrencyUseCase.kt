package com.faris.domain.usecases

import com.faris.domain.common.ResultState
import com.faris.domain.entity.response.currency.CurrencyEntity
import kotlinx.coroutines.flow.Flow

interface CurrencyUseCase {
    fun getSupportedCurrencies(): Flow<ResultState<CurrencyEntity.CurrencyList>>

    fun getCurrencyConversion(
        dateString: String,
        fromCurrency: String,
        toCurrency: String,
        amount: Double
    ): Flow<ResultState<CurrencyEntity.ConversionResult>>
}