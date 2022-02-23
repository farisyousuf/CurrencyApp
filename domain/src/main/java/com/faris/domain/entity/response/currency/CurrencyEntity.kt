package com.faris.domain.entity.response.currency

sealed class CurrencyEntity {
    data class CurrencyList(val currencyList: List<Currency>) : CurrencyEntity()

    data class Currency(val code: String, val value: String) : CurrencyEntity()
}
