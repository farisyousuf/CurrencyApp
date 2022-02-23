package com.faris.data.mapper.dtotoentity

import com.faris.data.remote.dto.CurrencyDto
import com.faris.data.remote.dto.ErrorDto
import com.faris.domain.entity.response.ErrorEntity
import com.faris.domain.entity.response.currency.CurrencyEntity

fun CurrencyDto.CurrencyList.map(): CurrencyEntity.CurrencyList {

    val currencyList = arrayListOf<CurrencyEntity.Currency>()
    currenciesJson?.entrySet()?.filter { !it.key.isNullOrEmpty() }?.mapNotNullTo(currencyList) {
        CurrencyEntity.Currency(it.key, it.value.asString)
    }
    return CurrencyEntity.CurrencyList(
        currencyList = currencyList,
        if (this.isSuccess == true) null else error?.map()
    )
}

fun CurrencyDto.ConversionResultDto.map(): CurrencyEntity.ConversionResult {
    val currencyList = arrayListOf<CurrencyEntity.Currency>()
    ratesJson?.entrySet()?.filter { !it.key.isNullOrEmpty() }?.mapNotNullTo(currencyList) {
        CurrencyEntity.Currency(code = it.key, rate = it.value.asDouble, value = it.key)
    }
    return CurrencyEntity.ConversionResult(
        fromCurrency = fromCurrency ?: "",
        currencyListWithRates = currencyList,
        if (this.success == true) null else error?.map()
    )
}

fun ErrorDto.Error.map(): ErrorEntity.Error {
    return ErrorEntity.Error(
        errorCode = code,
        errorMessage = if (!message.isNullOrEmpty()) message else type
    )
}