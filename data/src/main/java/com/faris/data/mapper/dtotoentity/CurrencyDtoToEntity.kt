package com.faris.data.mapper.dtotoentity

import com.faris.data.remote.dto.CurrencyDto
import com.faris.domain.entity.response.currency.CurrencyEntity

fun CurrencyDto.CurrencyList.map(): CurrencyEntity.CurrencyList {

    val currencyList = arrayListOf<CurrencyEntity.Currency>()
    currenciesJson?.entrySet()?.filter { !it.key.isNullOrEmpty() }?.mapNotNullTo(currencyList) {
        CurrencyEntity.Currency(it.key, it.value.asString)
    }
    return CurrencyEntity.CurrencyList(currencyList = currencyList)
}