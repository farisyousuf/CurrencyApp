package com.faris.data.remote.dto

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

sealed class CurrencyDto {

    data class CurrencyList(
        @SerializedName("success")
        val isSuccess: Boolean? = false,
        @SerializedName("symbols")
        val currenciesJson: JsonObject? = null,
        @SerializedName("error")
        val error: ErrorDto.Error? = null
    ) : CurrencyDto()

    data class ConversionResultDto(

        @SerializedName("success") var success: Boolean? = null,
        @SerializedName("rates")
        val ratesJson: JsonObject? = null,
        @SerializedName("base") var fromCurrency: String? = null,
        @SerializedName("date") var dateString: String? = null,
        @SerializedName("error")
        val error: ErrorDto.Error? = null

    ) : CurrencyDto()
}