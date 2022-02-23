package com.faris.currency.di.module

import com.faris.domain.repository.CurrencyRepository
import com.faris.domain.usecases.CurrencyUseCase
import com.faris.domain.usecases.CurrencyUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Singleton
    @Provides
    fun provideCurrencyUseCase(repository: CurrencyRepository): CurrencyUseCase =
        CurrencyUseCaseImpl(repository)
}