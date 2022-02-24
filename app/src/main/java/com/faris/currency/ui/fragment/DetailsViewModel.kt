package com.faris.currency.ui.fragment

import androidx.databinding.ObservableArrayList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.faris.currency.BR
import com.faris.currency.R
import com.faris.currency.arc.SingleLiveEvent
import com.faris.currency.ui.models.RateItemViewModel
import com.faris.currency.util.Constants.HISTORY_DATE_SIZE
import com.faris.domain.common.ResultState
import com.faris.domain.entity.response.ErrorEntity
import com.faris.domain.usecases.CurrencyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import me.tatarka.bindingcollectionadapter2.ItemBinding
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(private val useCase: CurrencyUseCase) : ViewModel() {

    val loadingEvent = SingleLiveEvent<Boolean>()
    val errorEvent = SingleLiveEvent<ErrorEntity.Error?>()
    var items = ObservableArrayList<RateItemViewModel>()
    val itemBinding: ItemBinding<RateItemViewModel> =
        ItemBinding.of(
            BR.viewModel,
            R.layout.rates_item
        )

    /**
     * Method to historic rate data of 30 days
     */
    fun getHistory(fromCurrency: String, toCurrency: String) {
        showLoading(true)
        viewModelScope.launch {
            useCase.getCurrencyConversionByDays(
                HISTORY_DATE_SIZE,
                fromCurrency,
                toCurrency
            ).collect { result ->
                when (result) {
                    is ResultState.Success -> {
                        items.addAll(result.data.map {
                            RateItemViewModel(it, toCurrency)
                        })
                    }
                    is ResultState.Error -> {
                        showLoading(false)
                        showError(result.error)
                    }
                }
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