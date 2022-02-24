package com.faris.data.repository

import android.text.format.DateFormat
import com.faris.data.mapper.dtotoentity.map
import com.faris.data.remote.api.CurrencyApi
import com.faris.data.util.Constants.SERVER_DATE_FORMAT
import com.faris.data.util.serverFormattedDateString
import com.faris.domain.common.ResultState
import com.faris.domain.entity.response.ErrorEntity
import com.faris.domain.entity.response.currency.CurrencyEntity
import com.faris.domain.repository.CurrencyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*
import javax.inject.Inject

class CurrencyRepositoryImpl @Inject constructor(private val currencyApi: CurrencyApi) :
    BaseRepositoryImpl(),
    CurrencyRepository {
    override fun getSupportedCurrencies(): Flow<ResultState<CurrencyEntity.CurrencyList>> = flow {
        emit(apiCall { currencyApi.getCurrencies().map() })
    }

    override fun getCurrencyConversion(
        fromCurrency: String,
        toCurrency: String
    ): Flow<ResultState<CurrencyEntity.ConversionResult>> = flow {
        emit(apiCall {
            currencyApi.convert(
                date = Calendar.getInstance().time.serverFormattedDateString(),
                from = fromCurrency,
                to = toCurrency
            ).map()
        })
    }

    override fun getCurrencyConversionByDays(
        days: Int,
        fromCurrency: String,
        toCurrency: String
    ): Flow<ResultState<List<CurrencyEntity.ConversionResult>>> = flow {
        val start = Calendar.getInstance()
        start.add(Calendar.DATE, -days)
        val end = Calendar.getInstance()
        val results = arrayListOf<CurrencyEntity.ConversionResult>()
        while (start.before(end)) {
            val result = apiCall {
                currencyApi.convert(
                    date = "${DateFormat.format(SERVER_DATE_FORMAT, start.time)}",
                    from = fromCurrency,
                    to = toCurrency
                )
            }
            if (result is ResultState.Success) {
                result.data.takeIf { it.success ?: false }?.map()?.let {
                    results.add(it)
                }
            }
            start.add(Calendar.DATE, 1)
        }
        if (results.isNotEmpty()) {
            emit(ResultState.Success(results.toList()))
        } else {
            emit(ResultState.Error(ErrorEntity.Error("No data", errorMessage = "No data")))
        }
    }
}