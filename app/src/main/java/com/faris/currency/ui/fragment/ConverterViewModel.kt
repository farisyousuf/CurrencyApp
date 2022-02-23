package com.faris.currency.ui.fragment

import android.text.format.DateFormat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faris.currency.arc.SingleLiveEvent
import com.faris.domain.common.ResultState
import com.faris.domain.entity.response.ErrorEntity
import com.faris.domain.entity.response.currency.CurrencyEntity
import com.faris.domain.usecases.CurrencyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ConverterViewModel @Inject constructor(private val currencyUseCase: CurrencyUseCase) :
    ViewModel() {

    val loadingEvent = SingleLiveEvent<Boolean>()
    val errorEvent = SingleLiveEvent<ErrorEntity.Error?>()
    val switchCurrencies = SingleLiveEvent<Unit>()
    val goToDetailsScreen = SingleLiveEvent<Unit>()

    private var _currencyList = MutableLiveData<List<CurrencyEntity.Currency>>()
    val currencyList: LiveData<List<CurrencyEntity.Currency>> = _currencyList

    private var _fromCurrency = MutableLiveData<CurrencyEntity.Currency?>()
    var fromCurrency: LiveData<CurrencyEntity.Currency?> = _fromCurrency

    private var _toCurrency = MutableLiveData<CurrencyEntity.Currency?>()
    val toCurrency: LiveData<CurrencyEntity.Currency?> = _toCurrency

    private var _fromAmount = MutableLiveData<String>()
    var fromAmount: LiveData<String> = _fromAmount
    private var _toAmount = MutableLiveData<String>()
    var toAmount: LiveData<String> = _toAmount

    init {
        getCurrencyList()
    }

    /**
     * Initial call in viewModel to fetch the Currency List
     */
    private fun getCurrencyList() {
        showLoading(true)
        viewModelScope.launch {
            currencyUseCase.getSupportedCurrencies().collect { result ->
                when (result) {
                    is ResultState.Success -> {
                        showLoading(false)
                        _currencyList.value = result.data.currencyList
                    }
                    is ResultState.Error -> {
                        showLoading(false)
                        showError(result.error)
                    }
                }
                Timber.e("Result: %s", result.toString())
            }
        }
    }

    /**
     * Method to invoke switching of currencies event
     */
    fun onSwitchCurrenciesClicked() {
        switchCurrencies.call()
    }

    fun onDetailsClicked() {
        goToDetailsScreen.call()
    }

    /**
     * Method to convert amount
     * amountString: The amount from input field
     * isToAmountChanged: Boolean to check if the value is changed in to Amount FIeld
     */
    fun convert(amountString: String, isToAmountChanged: Boolean = false) {
        val fromCurrency: String? =
            if (isToAmountChanged) _toCurrency.value?.code else _fromCurrency.value?.code
        val toCurrency: String? =
            if (isToAmountChanged) _fromCurrency.value?.code else _toCurrency.value?.code

        if (fromCurrency == toCurrency
            || fromCurrency.isNullOrEmpty()
            || toCurrency.isNullOrEmpty()
        ) return

        val amount = amountString.toDoubleOrNull()
        if (amount == null) {
            showError(ErrorEntity.Error(errorCode = "INVALID", errorMessage = "Invalid amount"))
            return
        }
        convert(fromCurrency, toCurrency, amount, isToAmountChanged)
    }

    /**
     * Private function in viewmodel
     * accessing the usecase to fetch the
     * currency conversion rate
     * fromCurrency: Base currency selected
     * toCurrency: Currency to convert to
     * amount: Amount to be converted
     * isToAmount: Boolean to specify that selected amount is from To Amount Field
     */
    private fun convert(
        fromCurrency: String,
        toCurrency: String,
        amount: Double,
        isToAmount: Boolean = false
    ) {
        showLoading(true)
        viewModelScope.launch {
            currencyUseCase.getCurrencyConversion(
                fromCurrency,
                toCurrency
            ).collect { result ->
                when (result) {
                    is ResultState.Success -> {
                        showLoading(false)
                        if (!checkHasError(result.data.error)) {
                            result.data.error?.let {
                                clearAmounts()
                                errorEvent.value = result.data.error
                                return@collect
                            }
                            if (isToAmount) {
                                _fromAmount.value = getResultAmount(
                                    result.data.currencyListWithRates.find { it.code == fromCurrency }?.rate,
                                    amount
                                )
                            } else {
                                _toAmount.value = getResultAmount(
                                    result.data.currencyListWithRates.find { it.code == toCurrency }?.rate,
                                    amount
                                )
                            }
                        } else {
                            clearAmounts()
                        }
                    }
                    is ResultState.Error -> {
                        showLoading(false)
                        showError(result.error)
                        clearAmounts()
                    }
                }
            }
        }
    }

    /**
     * Checking if the response returned contains error
     */
    private fun checkHasError(errorEntity: ErrorEntity.Error?): Boolean {
        return errorEntity?.let {
            errorEvent.value = it
            true
        } ?: false
    }

    /**
     * Clear the amount values
     */
    fun clearAmounts() {
        _fromAmount.value = ""
        _toAmount.value = ""
    }

    /**
     * Returns the String value containing the converted amount
     */
    private fun getResultAmount(rate: Double?, amount: Double): String {
        return "${rate?.times(amount) ?: ""}"
    }

    /**
     * Method to show Loading dialog
     */
    private fun showLoading(shouldShow: Boolean) {
        loadingEvent.value = shouldShow
    }

    /**
     * Method to show error
     */
    private fun showError(error: ErrorEntity.Error?) {
        errorEvent.value = error
    }

    /**
     * Method to set from currency
     */
    fun setFromCurrency(currency: CurrencyEntity.Currency) {
        _fromCurrency.value = currency
    }

    /**
     * Method to set To currency
     */
    fun setToCurrency(currency: CurrencyEntity.Currency) {
        _toCurrency.value = currency
    }
}