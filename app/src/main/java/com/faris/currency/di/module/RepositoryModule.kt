package com.faris.currency.di.module

import com.faris.data.remote.api.CurrencyApi
import com.faris.data.repository.CurrencyRepositoryImpl
import com.faris.domain.repository.CurrencyRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideCurrencyRepository(currencyApi: CurrencyApi): CurrencyRepository {
        return CurrencyRepositoryImpl(currencyApi)
    }
}