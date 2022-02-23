package com.faris.data.remote.dto

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

sealed class CurrencyDto {

    data class CurrencyList(
        @SerializedName("success")
        val isSuccess: Boolean? = false,
        @SerializedName("symbols")
        val currenciesJson: JsonObject? = null
    ) : CurrencyDto()
}