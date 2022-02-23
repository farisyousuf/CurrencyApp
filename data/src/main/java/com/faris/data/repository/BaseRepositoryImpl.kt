package com.faris.data.repository

import android.util.MalformedJsonException
import com.faris.data.remote.dto.ErrorDto
import com.faris.data.util.NetworkConstants
import com.faris.domain.common.ResultState
import com.faris.domain.entity.response.ErrorEntity
import com.google.gson.GsonBuilder
import okhttp3.ResponseBody
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import java.io.InterruptedIOException
import java.lang.Exception
import java.net.SocketException
import java.net.SocketTimeoutException

abstract class BaseRepositoryImpl {
    private val logFormatter: String = "%s | %s"
    protected suspend fun <T : Any> apiCall(call: suspend () -> T): ResultState<T> {
        return try {
            val response = call()
            ResultState.Success(response)
        } catch (e: Throwable) {
            ResultState.Error(handleError(e))
        }
    }

    private fun handleError(throwable: Throwable): ErrorEntity.Error {
        return when (throwable) {
            is SocketTimeoutException, is SocketException, is InterruptedIOException -> {
                Timber.e(
                    logFormatter,
                    throwable.message.toString(),
                    NetworkConstants.NetworkErrorMessages.SERVICE_UNAVAILABLE
                )

                ErrorEntity.Error(
                    NetworkConstants.NetworkErrorCodes.SERVICE_UNAVAILABLE,
                    NetworkConstants.NetworkErrorMessages.SERVICE_UNAVAILABLE
                )
            }
            is MalformedJsonException -> {
                Timber.e(
                    logFormatter,
                    throwable.message.toString(),
                    NetworkConstants.NetworkErrorMessages.MALFORMED_JSON
                )

                ErrorEntity.Error(
                    NetworkConstants.NetworkErrorCodes.MALFORMED_JSON,
                    NetworkConstants.NetworkErrorMessages.MALFORMED_JSON
                )
            }
            is IOException -> {
                Timber.e(
                    logFormatter,
                    throwable.message.toString(),
                    NetworkConstants.NetworkErrorMessages.NO_INTERNET
                )

                ErrorEntity.Error(
                    NetworkConstants.NetworkErrorCodes.NO_INTERNET,
                    NetworkConstants.NetworkErrorMessages.NO_INTERNET
                )
            }
            is HttpException -> {
                Timber.e(
                    logFormatter,
                    throwable.response()?.errorBody(),
                    throwable.message().toString()
                )
                val errorResponse: ErrorDto.ErrorResponse? =
                    getError(throwable.response()?.errorBody())

                if (errorResponse?.error == null) {
                    ErrorEntity.Error(
                        NetworkConstants.NetworkErrorCodes.UNEXPECTED_ERROR,
                        NetworkConstants.NetworkErrorMessages.UNEXPECTED_ERROR
                    )
                } else {
                    ErrorEntity.Error(
                        errorResponse.error.code,
                        errorResponse.error.message.toString()
                    )
                }
                ErrorEntity.Error(
                    NetworkConstants.NetworkErrorCodes.NO_INTERNET,
                    NetworkConstants.NetworkErrorMessages.NO_INTERNET
                )
            }
            else -> {
                Timber.e(
                    logFormatter,
                    NetworkConstants.NetworkErrorCodes.UNEXPECTED_ERROR,
                    NetworkConstants.NetworkErrorMessages.UNEXPECTED_ERROR
                )

                ErrorEntity.Error(
                    NetworkConstants.NetworkErrorCodes.UNEXPECTED_ERROR,
                    NetworkConstants.NetworkErrorMessages.UNEXPECTED_ERROR
                )
            }
        }
    }

    private fun getError(errorBody: ResponseBody?): ErrorDto.ErrorResponse? {
        return try {
            val response = GsonBuilder().create()
                .fromJson(errorBody?.string(), ErrorDto.ErrorResponse::class.java)
            Timber.e("API error object: %s", response?.toString())
            response
        } catch (e: Exception) {
            ErrorDto.ErrorResponse(
                isSuccess = false,
                error = ErrorDto.Error(
                    NetworkConstants.NetworkErrorCodes.UNEXPECTED_ERROR,
                    NetworkConstants.NetworkErrorMessages.UNEXPECTED_ERROR
                )
            )
        }
    }
}