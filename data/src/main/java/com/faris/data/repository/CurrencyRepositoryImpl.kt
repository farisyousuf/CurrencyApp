package com.faris.data.repository

import com.faris.data.mapper.dtotoentity.map
import com.faris.data.remote.api.CurrencyApi
import com.faris.data.util.serverFormattedDateString
import com.faris.domain.common.ResultState
import com.faris.domain.entity.response.ErrorEntity
import com.faris.domain.entity.response.currency.CurrencyEntity
import com.faris.domain.repository.CurrencyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.*
import javax.inject.Inject

class CurrencyRepositoryImpl @Inject constructor(private val currencyApi: CurrencyApi) :
    BaseRepositoryImpl(),
    CurrencyRepository {
    override fun getSupportedCurrencies(): Flow<ResultState<CurrencyEntity.CurrencyList>> = flow {
        val result = apiCall { currencyApi.getCurrencies().map() }
        if (result is ResultState.Success) {
            if (!result.data.isSuccess) {
                emit(
                    ResultState.Error(
                        ErrorEntity.Error(
                            errorCode = result.data.error?.errorCode,
                            errorMessage = result.data.error?.errorMessage
                        )
                    )
                )
            } else {
                emit(result)
            }
        }
    }.flowOn(Dispatchers.IO)

    override fun getCurrencyConversion(
        fromCurrency: String,
        toCurrency: List<String>
    ): Flow<ResultState<CurrencyEntity.ConversionResult>> = flow {
        val result = apiCall {
            currencyApi.convertLatest(
                from = fromCurrency,
                to = toCurrency.joinToString(",") { it }
            ).map()
        }
        if (result is ResultState.Success) {
            if (!result.data.isSuccess) {
                emit(
                    ResultState.Error(
                        ErrorEntity.Error(
                            errorCode = result.data.error?.errorCode,
                            errorMessage = result.data.error?.errorMessage
                        )
                    )
                )
            } else {
                emit(result)
            }
        }
    }.flowOn(Dispatchers.IO)

    override fun getCurrencyConversionByDays(
        days: Int,
        fromCurrency: String,
        toCurrency: List<String>
    ): Flow<ResultState<List<CurrencyEntity.ConversionResult>>> = flow {
        val start = Calendar.getInstance()
        //Fixed issue where extra item was showing
        start.add(Calendar.DATE, -days + 1)
        start.set(Calendar.HOUR, 0)
        start.set(Calendar.MINUTE, 0)
        start.set(Calendar.SECOND, 0)
        val end = Calendar.getInstance()

        val results = arrayListOf<CurrencyEntity.ConversionResult>()
        while (start.before(end)) {
            val result = apiCall {
                currencyApi.convert(
                    date = start.time.serverFormattedDateString(),
                    from = fromCurrency,
                    to = toCurrency.joinToString(",") {it}
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
    }.flowOn(Dispatchers.IO)
}