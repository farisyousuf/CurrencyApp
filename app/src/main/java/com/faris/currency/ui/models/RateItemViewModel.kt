package com.faris.currency.ui.models

import com.faris.domain.entity.response.currency.CurrencyEntity

class RateItemViewModel(
    val rate: Double?,
    private val date: String,
    private val fromCurrency: String,
    private val toCurrency: String
) {
    fun getDateString() : String {
        return "On $date"
    }

    fun getToText() : String {
        return "1 $fromCurrency = $rate $toCurrency"
    }

    private fun getRate(): String {
        return rate.toString()
    }
}