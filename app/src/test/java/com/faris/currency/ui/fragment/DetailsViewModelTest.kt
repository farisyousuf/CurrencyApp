package com.faris.currency.ui.fragment

import com.faris.currency.*
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
        val toCurrency = "AED"
        //GIVEN
        val flowCurrencies = flowOf(ResultState.Success(getDummyConversionHistoryResult(fromCurrency, toCurrency)))

        //WHEN
        Mockito.doReturn(flowCurrencies).`when`(currencyUseCase).getCurrencyConversionByDays(HISTORY_DATE_SIZE, fromCurrency, toCurrency)

        viewModel.getHistory(fromCurrency, toCurrency)
        val rateHistoryList = viewModel.items
        //THEN
        Truth.assertThat(rateHistoryList.size).isGreaterThan(1)
    }

    @Test
    fun `given error result when getHistory should return error`() = runBlockingMainTest {
        val fromCurrency = "AED"
        val toCurrency = "AED"
        //GIVEN
        val flowCurrencies = flowOf(ResultState.Error<CurrencyEntity.ConversionResult>(getDummyError()))

        //WHEN
        Mockito.doReturn(flowCurrencies).`when`(currencyUseCase).getCurrencyConversionByDays(HISTORY_DATE_SIZE, fromCurrency, toCurrency)

        viewModel.getHistory(fromCurrency, toCurrency)
        val rateHistoryList = viewModel.items
        //THEN
        Truth.assertThat(rateHistoryList.size).isEqualTo(0)

        val error = viewModel.errorEvent.getOrAwaitValueTest()
        Truth.assertThat(error).isNotNull()
    }
}