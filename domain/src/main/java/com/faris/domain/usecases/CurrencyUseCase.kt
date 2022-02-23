package com.faris.domain.usecases

import com.faris.domain.common.ResultState
import com.faris.domain.entity.response.currency.CurrencyEntity
import kotlinx.coroutines.flow.Flow

interface CurrencyUseCase {
    fun getSupportedCurrencies() : Flow<ResultState<CurrencyEntity.CurrencyList>>
}