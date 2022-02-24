package com.faris.data

import com.faris.data.remote.dto.CurrencyDto
import com.google.gson.JsonObject

fun getDummyCurrencies(): CurrencyDto.CurrencyList {
    return CurrencyDto.CurrencyList(
        isSuccess = true,
        currenciesJson = JsonObject().apply {
            addProperty("AED", "United Arab Dirham")
            addProperty("USD", "United States Dollar")
            addProperty("EUR", "Euro")
        }
    )
}

fun getDummyConversionResult(): CurrencyDto.ConversionResultDto {
    return  CurrencyDto.ConversionResultDto(
        success = true,
        ratesJson = JsonObject().apply {
            addProperty("AED", 1.5)
            addProperty("USD", 4.5)
        },
        fromCurrency = "EUR",
        dateString = "2022-02-02"
    )
}