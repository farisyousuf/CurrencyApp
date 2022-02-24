package com.faris.currency

import com.faris.domain.common.ResultState
import com.faris.domain.entity.response.ErrorEntity
import com.faris.domain.entity.response.currency.CurrencyEntity

fun getDummyCurrencies(): CurrencyEntity.CurrencyList {
    return CurrencyEntity.CurrencyList(
        currencyList = listOf(
            CurrencyEntity.Currency(
                code = "AED",
                value = "United Arab Dirham",
            ), CurrencyEntity.Currency(
                code = "USD",
                value = "United States Dollar",
            ), CurrencyEntity.Currency(
                code = "EUR",
                value = "Euro",
            )
        )
    )
}

fun getDummyErrorResultStateCurrencyList(): ResultState<CurrencyEntity.CurrencyList> {
    return ResultState.Error(getDummyError())
}

fun getDummyError(): ErrorEntity.Error? {
    return ErrorEntity.Error(
        errorCode = "FAILED",
        errorMessage = "Failed to get Response"
    )
}

fun getDummyConversionResult(
    fromCurrency: String,
    toCurrency: String
): CurrencyEntity.ConversionResult {
    return CurrencyEntity.ConversionResult(
        fromCurrency = fromCurrency,
        currencyListWithRates = listOf(
            CurrencyEntity.Currency(code = toCurrency, value = toCurrency, rate = 12.5),
        ),
        dateString = "2022-06-02"
    )
}

fun getDummyConversionHistoryResult(
    fromCurrency: String,
    toCurrency: String
): List<CurrencyEntity.ConversionResult> {
    return listOf(
        CurrencyEntity.ConversionResult(
            fromCurrency = fromCurrency,
            currencyListWithRates = listOf(
                CurrencyEntity.Currency(code = toCurrency, value = toCurrency, rate = 12.5),
            ),
            dateString = "2022-06-02"
        ),
        CurrencyEntity.ConversionResult(
            fromCurrency = fromCurrency,
            currencyListWithRates = listOf(
                CurrencyEntity.Currency(code = toCurrency, value = toCurrency, rate = 12.51),
            ),
            dateString = "2022-06-01"
        )
    )
}