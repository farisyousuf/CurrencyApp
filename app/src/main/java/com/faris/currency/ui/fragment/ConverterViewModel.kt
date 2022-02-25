package com.faris.currency.ui.fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faris.currency.arc.SingleLiveEvent
import com.faris.currency.util.Constants.BASE_CURRENCY
import com.faris.currency.util.CurrencyUtil
import com.faris.domain.common.ResultState
import com.faris.domain.entity.response.ErrorEntity
import com.faris.domain.entity.response.currency.CurrencyEntity
import com.faris.domain.usecases.CurrencyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ConverterViewModel @Inject constructor(private val currencyUseCase: CurrencyUseCase) :
    ViewModel() {

    val loadingEvent = SingleLiveEvent<Boolean>()
    val errorEvent = SingleLiveEvent<ErrorEntity.Error?>()
    val switchAmount = SingleLiveEvent<Unit>()
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

    /**
     * Initial call in viewModel to fetch the Currency List
     */
    fun getCurrencyList() {
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
    fun onSwitchAmountClicked() {
        switchAmount.call()
    }

    /**
     * Method to navigate user to Details screen
     */
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
        ) {
            resetAmount()
            return
        }

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
        //Here we pass the base currency always
        //as EUR and the converted amount is calculated
        //based on its results
        viewModelScope.launch {
            currencyUseCase.getCurrencyConversion(
                BASE_CURRENCY,
                listOf(fromCurrency, toCurrency)
            ).collect { result ->
                when (result) {
                    is ResultState.Success -> {
                        showLoading(false)
                        val convertedAmount = CurrencyUtil.getConvertedAmount(
                            fromCurrency,
                            toCurrency,
                            result.data.currencyListWithRates,
                            amount
                        )
                        if (null != convertedAmount) {
                            if(isToAmount) {
                                _fromAmount.value = convertedAmount.toString()
                            } else {
                                _toAmount.value = convertedAmount.toString()
                            }
                        } else {
                            showError(ErrorEntity.Error(errorCode = "Error", errorMessage = "Something Went wrong"))
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
     * Clear the amount values
     */
    private fun clearAmounts() {
        setFromAmount("")
        setToAmount("")
    }

    private fun resetAmount() {
        setFromAmount("1")
        setToAmount("1")
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

    /**
     * Method to set From amount
     */
    fun setFromAmount(fromAmount: String) {
        _fromAmount.value = fromAmount
    }

    /**
     * Method to set To amount
     */
    fun setToAmount(toAmount: String) {
        _toAmount.value = toAmount
    }
}