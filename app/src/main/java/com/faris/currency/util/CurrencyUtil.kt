package com.faris.currency.util

import com.faris.currency.util.extensions.roundTo
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

        return if (null == fromCurrencyEuroRate || null == toCurrencyEuroRate) {
            null
        } else {
            (toCurrencyEuroRate / fromCurrencyEuroRate * amount).roundTo(3)
        }
    }
}