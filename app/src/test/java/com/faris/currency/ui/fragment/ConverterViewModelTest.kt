package com.faris.currency.ui.fragment

import com.faris.currency.*
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
class ConverterViewModelTest : BaseViewModelTest() {

    @Mock
    private lateinit var currencyUseCase: CurrencyUseCase

    private lateinit var converterViewModel: ConverterViewModel

    @Before
    fun setUp() {
        converterViewModel = ConverterViewModel(currencyUseCase)
    }

    @Test
    fun `given currencies when getCurrencyList should return success`() = runBlockingMainTest {

        //GIVEN
        val flowCurrencies = flowOf(ResultState.Success(getDummyCurrencies()))

        //WHEN
        Mockito.doReturn(flowCurrencies).`when`(currencyUseCase).getSupportedCurrencies()

        converterViewModel.getCurrencyList()
        val currenciesList = converterViewModel.currencyList.getOrAwaitValueTest()
        //THEN
        Truth.assertThat(currenciesList).isNotEmpty()
    }

    @Test
    fun `given error when getCurrencyList should return error`() = runBlockingMainTest {
        //GIVEN
        val flowCurrenciesError = flowOf(getDummyError())

        //WHEN
        Mockito.doReturn(flowCurrenciesError).`when`(currencyUseCase).getSupportedCurrencies()

        converterViewModel.getCurrencyList()
        val error = converterViewModel.errorEvent.getOrAwaitValueTest()
        //THEN
        Truth.assertThat(error?.errorCode).isNotNull()
    }

    @Test
    fun `given invalid amount in convert should return error`() = runBlockingMainTest {
        converterViewModel.setFromCurrency(CurrencyEntity.Currency(code = "AED", value = "AED"))
        converterViewModel.setToCurrency(CurrencyEntity.Currency(code = "USD", value = "USD"))
        converterViewModel.convert("a")

        val error = converterViewModel.errorEvent.getOrAwaitValueTest()
        Truth.assertThat(error?.errorCode).isNotNull()
    }

    @Test
    fun `given same currency in convert should reset amount`() = runBlockingMainTest {
        converterViewModel.setFromCurrency(CurrencyEntity.Currency(code = "AED", value = "AED"))
        converterViewModel.setToCurrency(CurrencyEntity.Currency(code = "AED", value = "AED"))
        converterViewModel.convert("a")

        val fromAmount = converterViewModel.fromAmount.getOrAwaitValueTest()
        val toAmount = converterViewModel.toAmount.getOrAwaitValueTest()

        Truth.assertThat(fromAmount).isEqualTo(toAmount)
    }

    @Test
    fun `given valid response in convert for valid amount input should return success`() =
        runBlockingMainTest {
            val fromAmount = 10.0
            converterViewModel.setFromCurrency(CurrencyEntity.Currency(code = "AED", value = "AED"))
            converterViewModel.setToCurrency(CurrencyEntity.Currency(code = "USD", value = "USD"))
            //GIVEN
            val dummyResult = getDummyConversionResult("AED", "USD")
            val flowCurrenciesRates = flowOf(ResultState.Success(dummyResult))
            Mockito.doReturn(flowCurrenciesRates).`when`(currencyUseCase)
                .getCurrencyConversion("AED", "USD")
            converterViewModel.convert(fromAmount.toString())

            val toAmount = converterViewModel.toAmount.getOrAwaitValueTest()
            val calculatedTo =
                fromAmount.times(dummyResult.currencyListWithRates.first().rate!!)
            Truth.assertThat(toAmount).isEqualTo(calculatedTo.toString())
        }

    @Test
    fun `given valid response in convert for valid to amount input should return success`() =
        runBlockingMainTest {
            val toAmount = 10.0
            converterViewModel.setFromCurrency(CurrencyEntity.Currency(code = "AED", value = "AED"))
            converterViewModel.setToCurrency(CurrencyEntity.Currency(code = "USD", value = "USD"))
            //GIVEN
            val dummyResult = getDummyConversionResult("USD", "AED")
            val flowCurrenciesRates = flowOf(ResultState.Success(dummyResult))
            Mockito.doReturn(flowCurrenciesRates).`when`(currencyUseCase)
                .getCurrencyConversion("USD", "AED")
            converterViewModel.convert(toAmount.toString(), isToAmountChanged = true)

            val fromAmount = converterViewModel.fromAmount.getOrAwaitValueTest()
            Truth.assertThat(fromAmount).isNotEmpty()
            val calculatedFrom =
                toAmount.times(dummyResult.currencyListWithRates.first().rate!!)
            Truth.assertThat(fromAmount).isEqualTo(calculatedFrom.toString())
        }

    @Test
    fun `given response with error should return error`() = runBlockingMainTest {
        val toAmount = 10.0
        converterViewModel.setFromCurrency(CurrencyEntity.Currency(code = "AED", value = "AED"))
        converterViewModel.setToCurrency(CurrencyEntity.Currency(code = "USD", value = "USD"))
        //GIVEN
        val dummyResult = getDummyConversionResultWithError()
        val flowCurrenciesRates = flowOf(ResultState.Success(dummyResult))
        Mockito.doReturn(flowCurrenciesRates).`when`(currencyUseCase)
            .getCurrencyConversion("USD", "AED")
        converterViewModel.convert(toAmount.toString(), isToAmountChanged = true)

        val fromAmount = converterViewModel.fromAmount.getOrAwaitValueTest()
        val error = converterViewModel.errorEvent.getOrAwaitValueTest()
        Truth.assertThat(fromAmount).isEmpty()
        Truth.assertThat(error?.errorCode).isEqualTo(dummyResult.error?.errorCode)
    }

    @Test
    fun `test switch click event reset switch amount`() = runBlockingMainTest {
        converterViewModel.onSwitchAmountClicked()
        converterViewModel.switchAmount.getOrAwaitValueTest {
            converterViewModel.setFromAmount("1")
            converterViewModel.setToAmount("2")
        }
        val fromAmount = converterViewModel.fromAmount.getOrAwaitValueTest()
        val toAmount = converterViewModel.toAmount.getOrAwaitValueTest()

        Truth.assertThat(fromAmount).isEqualTo("1")
        Truth.assertThat(toAmount).isEqualTo("2")
    }

    @Test
    fun `test go to details click event triggers event`() = runBlockingMainTest {
        var navigated = false
        converterViewModel.onDetailsClicked()
        converterViewModel.goToDetailsScreen.getOrAwaitValueTest {
            navigated = true
        }
        Truth.assertThat(navigated).isTrue()
    }

}