package com.faris.currency.ui.fragment

import com.faris.currency.*
import com.faris.currency.util.Constants.BASE_CURRENCY
import com.faris.currency.util.Constants.HISTORY_DATE_SIZE
import com.faris.domain.common.ResultState
import com.faris.domain.entity.response.currency.CurrencyEntity
import com.faris.domain.usecases.CurrencyUseCase
import com.google.common.truth.Truth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class DetailsViewModelTest : BaseViewModelTest() {
    @Mock
    private lateinit var currencyUseCase: CurrencyUseCase

    private lateinit var viewModel: DetailsViewModel

    @Before
    fun setup() {
        viewModel = DetailsViewModel(currencyUseCase)
    }

    @Test
    fun `given conversion results when getHistory should return success`() = runBlockingMainTest {
        val fromCurrency = "AED"
        val toCurrency = "USD"
        //GIVEN
        val flowCurrencies =
            flowOf(ResultState.Success(getDummyConversionHistoryResult(fromCurrency, toCurrency)))
        val currenciesList =
            flowOf(ResultState.Success(getDummyConversionResult(fromCurrency, toCurrency)))

        //WHEN
        Mockito.doReturn(flowCurrencies).`when`(currencyUseCase)
            .getCurrencyConversionByDays(HISTORY_DATE_SIZE, BASE_CURRENCY, listOf(fromCurrency, toCurrency))
        Mockito.doReturn(currenciesList).`when`(currencyUseCase)
            .getCurrencyConversion(
                BASE_CURRENCY,
                getFinalPopularCurrencyListTestData(fromCurrency, toCurrency)
            )

        viewModel.getData(fromCurrency, toCurrency)
        val rateHistoryList = viewModel.items
        val otherRatesList = viewModel.rateItems
        //THEN
        Truth.assertThat(rateHistoryList.size).isGreaterThan(0)
        Truth.assertThat(otherRatesList.size).isGreaterThan(0)
    }

    @Test
    fun `given conversion results when getHistory should populate chart list`() =
        runBlockingMainTest {
            val fromCurrency = "AED"
            val toCurrency = "USD"
            //GIVEN
            val flowCurrencies = flowOf(
                ResultState.Success(
                    getDummyConversionHistoryResult(
                        fromCurrency,
                        toCurrency
                    )
                )
            )
            val currenciesList =
                flowOf(ResultState.Success(getDummyConversionResult(fromCurrency, toCurrency)))
            //WHEN
            Mockito.doReturn(flowCurrencies).`when`(currencyUseCase)
                .getCurrencyConversionByDays(HISTORY_DATE_SIZE, BASE_CURRENCY, listOf(fromCurrency, toCurrency))
            Mockito.doReturn(currenciesList).`when`(currencyUseCase)
                .getCurrencyConversion(
                    BASE_CURRENCY,
                    getFinalPopularCurrencyListTestData(fromCurrency, toCurrency)
                )

            viewModel.getData(fromCurrency, toCurrency)
            val chartItems = viewModel.chartItems.getOrAwaitValueTest()
            //THEN
            Truth.assertThat(chartItems.size).isGreaterThan(0)
        }

    @Test
    fun `given error result when getHistory should return error`() = runBlockingMainTest {
        val fromCurrency = "AED"
        val toCurrency = "USD"
        //GIVEN
        val errorCurrencies =
            flowOf<ResultState<CurrencyEntity.ConversionResult>>(ResultState.Error(getDummyError()))

        //WHEN
        Mockito.doReturn(errorCurrencies).`when`(currencyUseCase)
            .getCurrencyConversionByDays(HISTORY_DATE_SIZE, BASE_CURRENCY, listOf(fromCurrency, toCurrency))
        Mockito.doReturn(errorCurrencies).`when`(currencyUseCase)
            .getCurrencyConversion(BASE_CURRENCY, getFinalPopularCurrencyListTestData(fromCurrency, toCurrency))
        viewModel.getData(fromCurrency, toCurrency)
        val rateHistoryList = viewModel.items
        //THEN
        Truth.assertThat(rateHistoryList.size).isEqualTo(0)
    }
}