package com.faris.domain.entity.response.currency

import com.faris.domain.entity.response.ErrorEntity

sealed class CurrencyEntity {
    data class CurrencyList(
        val isSuccess: Boolean = false,
        val currencyList: List<Currency>,
        val error: ErrorEntity.Error? = null
    ) : CurrencyEntity()

    data class Currency(val code: String, val value: String, val rate: Double? = null) : CurrencyEntity()

    data class ConversionResult(
        val isSuccess: Boolean = false,
        val fromCurrency: String,
        val currencyListWithRates: List<Currency>,
        val dateString: String,
        val error: ErrorEntity.Error? = null
    ) : CurrencyEntity()
}
