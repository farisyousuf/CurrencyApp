package com.faris.domain.usecases

import com.faris.domain.common.ResultState
import com.faris.domain.entity.response.currency.CurrencyEntity
import com.faris.domain.repository.CurrencyRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CurrencyUseCaseImpl @Inject constructor(private val currencyRepository: CurrencyRepository) : CurrencyUseCase {
    override fun getSupportedCurrencies(): Flow<ResultState<CurrencyEntity.CurrencyList>> {
        return currencyRepository.getSupportedCurrencies()
    }

    override fun getCurrencyConversion(
        fromCurrency: String,
        toCurrency: List<String>
    ): Flow<ResultState<CurrencyEntity.ConversionResult>> {
        return currencyRepository.getCurrencyConversion(fromCurrency, toCurrency)
    }

    override fun getCurrencyConversionByDays(
        days: Int,
        fromCurrency: String,
        toCurrency: List<String>
    ): Flow<ResultState<List<CurrencyEntity.ConversionResult>>> {
        return currencyRepository.getCurrencyConversionByDays(days, fromCurrency, toCurrency)
    }
}