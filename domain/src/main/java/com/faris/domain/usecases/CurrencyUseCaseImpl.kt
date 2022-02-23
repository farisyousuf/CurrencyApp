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
}