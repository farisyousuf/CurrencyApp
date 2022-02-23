package com.faris.currency.ui.fragment

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
import javax.inject.Inject

@HiltViewModel
class ConverterViewModel @Inject constructor(private val currencyUseCase: CurrencyUseCase) :
    ViewModel() {

    val loadingEvent = SingleLiveEvent<Boolean>()
    val errorEvent = SingleLiveEvent<ErrorEntity.Error?>()

    private var _currencyList =  MutableLiveData<List<CurrencyEntity.Currency>>()
    val currencyList: LiveData<List<CurrencyEntity.Currency>> = _currencyList

    private var _fromCurrency =  MutableLiveData<CurrencyEntity.Currency?>()
    var fromCurrency : LiveData<CurrencyEntity.Currency?> = _fromCurrency

    private var _toCurrency =  MutableLiveData<CurrencyEntity.Currency?>()
    val toCurrency : LiveData<CurrencyEntity.Currency?> = _toCurrency

    var fromAmount =  MutableLiveData("1")
    var toAmount =  MutableLiveData("1")

    init {
        getCurrencyList()
    }

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

    fun showLoading(shouldShow: Boolean) {
        loadingEvent.value = shouldShow
    }

    fun showError(error: ErrorEntity.Error?) {
        errorEvent.value = error
    }

    fun setFromCurrency(currency: CurrencyEntity.Currency) {
        _fromCurrency.value = currency
    }

    fun setToCurrency(currency: CurrencyEntity.Currency) {
        _toCurrency.value = currency
    }
}