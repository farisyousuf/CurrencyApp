package com.faris.domain.entity.response.currency

import com.faris.domain.entity.response.ErrorEntity

sealed class CurrencyEntity {
    data class CurrencyList(
        val currencyList: List<Currency>,
        val error: ErrorEntity.Error? = null
    ) : CurrencyEntity()

    data class Currency(val code: String, val value: String, val rate: Double? = null) : CurrencyEntity()

    data class ConversionResult(
        val fromCurrency: String,
        val currencyListWithRates: List<Currency>,
        val error: ErrorEntity.Error? = null
    ) : CurrencyEntity()
}
