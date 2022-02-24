package com.faris.currency.ui.fragment

import androidx.databinding.ObservableArrayList
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faris.currency.BR
import com.faris.currency.R
import com.faris.currency.arc.SingleLiveEvent
import com.faris.currency.ui.models.RateItemViewModel
import com.faris.currency.util.Constants
import com.faris.currency.util.Constants.HISTORY_DATE_SIZE
import com.faris.currency.util.Constants.getPopularCurrencies
import com.faris.currency.util.CurrencyUtil
import com.faris.domain.common.ResultState
import com.faris.domain.entity.response.ErrorEntity
import com.faris.domain.entity.response.currency.CurrencyEntity
import com.faris.domain.usecases.CurrencyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import me.tatarka.bindingcollectionadapter2.ItemBinding
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(private val useCase: CurrencyUseCase) : ViewModel() {

    val loadingEvent = SingleLiveEvent<Boolean>()
    val errorEvent = SingleLiveEvent<ErrorEntity.Error?>()
    var items = ObservableArrayList<RateItemViewModel>()
    var rateItems = ObservableArrayList<CurrencyEntity.Currency>()
    var chartItems = MutableLiveData<ArrayList<CurrencyEntity.ConversionResult>>()
    val itemBinding: ItemBinding<RateItemViewModel> =
        ItemBinding.of(
            BR.viewModel,
            R.layout.rates_item
        )
    val ratesItemBinding: ItemBinding<CurrencyEntity.Currency> =
        ItemBinding.of(
            BR.viewModel,
            R.layout.other_rates_item
        )

    /**
     * Method which gets the historic rates and
     * other currencies rates
     */
    fun getData(fromCurrency: String, toCurrency: String) {
        showLoading(true)
        val currencies = getPopularCurrencies()
        //Added the from and to currency to get their rates
        //in base currency EUR
        currencies.add(fromCurrency)
        currencies.add(toCurrency)
        viewModelScope.launch {
            useCase.getCurrencyConversionByDays(
                HISTORY_DATE_SIZE,
                "EUR",
                listOf(fromCurrency, toCurrency)
            ).combine(
                useCase.getCurrencyConversion("EUR", currencies)
            ) { historyData, otherRates ->
                when (historyData) {
                    is ResultState.Success -> {
                        //Calculating the rate and adding it to view
                        items.addAll(historyData.data.map {
                            val convertedToAmount = CurrencyUtil.getConvertedAmount(
                                fromCurrency,
                                toCurrency,
                                it.currencyListWithRates
                            )
                            RateItemViewModel(
                                convertedToAmount,
                                it.dateString,
                                fromCurrency,
                                toCurrency
                            )
                        }.filter {
                            it.rate != null
                        })
                        chartItems.value = ArrayList(historyData.data)
                    }
                    is ResultState.Error -> {
                        showError(historyData.error)
                    }
                }
                when (otherRates) {
                    is ResultState.Success -> {
                        //Here we get the rates of all popular currencies
                        //along with from currency and then find the rate base on EUR rate
                        val currencyList = otherRates.data.currencyListWithRates
                        val currencyListWithRates = currencyList.map {
                            val convertedToAmount = CurrencyUtil.getConvertedAmount(
                                fromCurrency,
                                it.code,
                                currencyList
                            )
                            it.apply {
                                rate = convertedToAmount
                            }
                        }
                        rateItems.addAll(currencyListWithRates.filter {
                            it.code != fromCurrency
                        })
                    }
                    is ResultState.Error -> {
                        showError(otherRates.error)
                    }
                }
            }.collect {
                showLoading(false)
            }
        }
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
}