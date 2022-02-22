package com.faris.domain.common

import com.faris.domain.entity.response.ErrorEntity

sealed class ResultState<T> {
    data class Success<T>(val data: T) : ResultState<T>()
    data class Error<T>(val error: ErrorEntity.Error?) : ResultState<T>()
}