package com.faris.data.util

import androidx.annotation.IntDef
import androidx.annotation.StringDef

object NetworkConstants {
    @IntDef(
        NetworkErrorCodes.SERVICE_UNAVAILABLE,
        NetworkErrorCodes.MALFORMED_JSON,
        NetworkErrorCodes.NO_INTERNET,
        NetworkErrorCodes.UNEXPECTED_ERROR
    )
    @Retention(AnnotationRetention.SOURCE)
    annotation class NetworkErrorCodes {
        companion object {
            const val SERVICE_UNAVAILABLE = 500
            const val MALFORMED_JSON = 501
            const val NO_INTERNET = 502
            const val UNEXPECTED_ERROR = 503
        }
    }

    @StringDef(
        NetworkErrorMessages.SERVICE_UNAVAILABLE,
        NetworkErrorMessages.MALFORMED_JSON,
        NetworkErrorMessages.NO_INTERNET,
        NetworkErrorMessages.UNEXPECTED_ERROR
    )
    @Retention(AnnotationRetention.SOURCE)
    annotation class NetworkErrorMessages {
        companion object {
            const val SERVICE_UNAVAILABLE =
                "System temporarily unavailable, please try again later."
            const val MALFORMED_JSON = "Oops! We hit an error. Try again later"
            const val NO_INTERNET = "Oh! You are not connected to internet."
            const val UNEXPECTED_ERROR = "Something went wrong"
        }
    }
}