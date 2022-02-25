package com.faris.currency.util

object Constants {
    //Limited the count since no of calls
    //for Fixer API is 100 for a free account
    const val HISTORY_DATE_SIZE = 6
    const val BASE_CURRENCY = "EUR"

    fun getPopularCurrencies() = arrayListOf(
        "USD",
        "AUD",
        "CAD",
        "PLN",
        "MXN",
        "INR",
        "AED",
        "AFN",
        "ALL",
        "AMD",
    )
}