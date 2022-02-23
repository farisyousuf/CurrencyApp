package com.faris.currency.ui.models

import com.faris.domain.entity.response.currency.CurrencyEntity

class RateItemViewModel(
    private val rateResult: CurrencyEntity.ConversionResult,
    private val toCurrency: String
) {
    fun getDateString() : String {
        return "On ${rateResult.dateString}"
    }

    fun getToText() : String {
        return "1 ${rateResult.fromCurrency} = ${getRate()} $toCurrency"
    }

    private fun getRate(): String {
        return "${rateResult.currencyListWithRates.find { it.code == toCurrency }?.rate ?: ""}"
    }
}