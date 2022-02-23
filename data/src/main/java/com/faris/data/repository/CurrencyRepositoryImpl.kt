package com.faris.data.repository

import com.faris.data.mapper.dtotoentity.map
import com.faris.data.remote.api.CurrencyApi
import com.faris.domain.common.ResultState
import com.faris.domain.entity.response.currency.CurrencyEntity
import com.faris.domain.repository.CurrencyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CurrencyRepositoryImpl @Inject constructor(private val currencyApi: CurrencyApi) :
    BaseRepositoryImpl(),
    CurrencyRepository {
    override fun getSupportedCurrencies(): Flow<ResultState<CurrencyEntity.CurrencyList>> = flow {
        emit(apiCall { currencyApi.getCurrencies().map() })
    }

    override fun getCurrencyConversion(
        dateString: String,
        fromCurrency: String,
        toCurrency: String,
        amount: Double
    ): Flow<ResultState<CurrencyEntity.ConversionResult>> = flow {
        emit(apiCall {
            currencyApi.convert(date = dateString, from = fromCurrency, to = toCurrency).map()
        })
    }
}