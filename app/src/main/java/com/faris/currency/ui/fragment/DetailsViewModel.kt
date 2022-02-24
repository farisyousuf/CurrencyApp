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
        viewModelScope.launch {
            useCase.getCurrencyConversionByDays(
                HISTORY_DATE_SIZE,
                fromCurrency,
                toCurrency
            ).combine(
                useCase.getCurrencyConversion(fromCurrency, currencies)
            ) { historyData, otherRates ->
                when (historyData) {
                    is ResultState.Success -> {
                        items.addAll(historyData.data.map {
                            RateItemViewModel(it, toCurrency)
                        })
                        chartItems.value = ArrayList(historyData.data)
                    }
                    is ResultState.Error -> {
                        showError(historyData.error)
                    }
                }
                when (otherRates) {
                    is ResultState.Success -> {
                        rateItems.addAll(otherRates.data.currencyListWithRates)
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