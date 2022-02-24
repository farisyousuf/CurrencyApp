package com.faris.data.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.faris.data.getDummyCurrencies
import com.faris.data.mapper.dtotoentity.map
import com.faris.data.remote.api.CurrencyApi
import com.faris.domain.common.ResultState
import com.faris.domain.entity.response.currency.CurrencyEntity
import com.faris.domain.repository.CurrencyRepository
import com.google.common.truth.Truth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class CurrencyRepositoryImplTest {
    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    private lateinit var repository: CurrencyRepository

    @Mock
    private lateinit var api: CurrencyApi

    @Before
    fun setup() {
        repository = CurrencyRepositoryImpl(api)
    }

    @Test
    fun `Given Currencies When getSupportedCurrencies returns Success`() = runBlocking {
        //GIVEN
        val givenCurrencies = getDummyCurrencies()
        val givenCurrenciesResult = givenCurrencies.map()
        val inputFlow = ResultState.Success(givenCurrenciesResult)
        Mockito.`when`(api.getCurrencies()).thenReturn(givenCurrencies)

        //WHEN
        val outputFlow = repository.getSupportedCurrencies()

        //THEN
        Truth.assertThat(outputFlow).isNotNull()
        var output: ResultState<CurrencyEntity.CurrencyList> = ResultState.Error(null)
        outputFlow.collect {
            output = it
        }
        Truth.assertThat(output).isEqualTo(inputFlow)
    }
}