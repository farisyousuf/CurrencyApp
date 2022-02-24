package com.faris.currency.util

import com.faris.domain.entity.response.currency.CurrencyEntity

object CurrencyUtil {
    fun getConvertedAmount(
        fromCurrency: String,
        toCurrency: String,
        currencyList: List<CurrencyEntity.Currency>,
        amount: Double = 1.0
    ): Double? {
        val fromCurrencyEuroRate: Double? = currencyList.find { it.code == fromCurrency }?.rate
        val toCurrencyEuroRate: Double? = currencyList.find { it.code == toCurrency }?.rate

        if (null == fromCurrencyEuroRate || null == toCurrencyEuroRate) {
            return null
        } else {
            return toCurrencyEuroRate / fromCurrencyEuroRate * amount
        }
    }
}