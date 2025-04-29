/**
 * A generic class that holds a value with its loading status.
 *
 * Result is usually created by the Repository classes where they return
 * `LiveData<Result<T>>` to pass back the latest data to the UI with its fetch status.
 * Please follow code style when editing project
 * Please follow principles of clean architecture
 * Created 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.domain.models.base

enum class ErrorType {
    EXCEPTION,
    SERVER_ERROR,
    ERROR_IN_LOGIC,
    SERVER_DATA_ERROR,
    NO_INTERNET_CONNECTION,
    AUTHORIZATION
}

data class ResultEmittedData<out T>(
    val model: T?,
    val error: Any?,
    val status: Status,
    val title: String?,
    val message: String?,
    val responseCode: Int?,
    val errorType: ErrorType?,
) {

    enum class Status {
        SUCCESS,
        ERROR,
        LOADING,
    }

    companion object {
        fun <T> success(
            model: T,
            message: String?,
            responseCode: Int?,
        ): ResultEmittedData<T> =
            ResultEmittedData(
                error = null,
                title = null,
                model = model,
                errorType = null,
                message = message,
                status = Status.SUCCESS,
                responseCode = responseCode,
            )

        fun <T> loading(
            model: T? = null,
            message: String? = null,
        ): ResultEmittedData<T> =
            ResultEmittedData(
                model = model,
                error = null,
                title = null,
                errorType = null,
                message = message,
                responseCode = null,
                status = Status.LOADING,
            )

        fun <T> error(
            model: T?,
            error: Any?,
            title: String?,
            message: String?,
            responseCode: Int?,
            errorType: ErrorType?,
        ): ResultEmittedData<T> =
            ResultEmittedData(
                model = model,
                error = error,
                title = title,
                message = message,
                errorType = errorType,
                status = Status.ERROR,
                responseCode = responseCode,
            )
    }
}

inline fun <T : Any> ResultEmittedData<T>.onLoading(
    action: (
        model: T?,
        message: String?,
    ) -> Unit
): ResultEmittedData<T> {
    if (status == ResultEmittedData.Status.LOADING) action(
        model,
        message
    )
    return this
}

inline fun <T : Any> ResultEmittedData<T>.onSuccess(
    action: (
        model: T,
        message: String?,
        responseCode: Int?,
    ) -> Unit
): ResultEmittedData<T> {
    if (status == ResultEmittedData.Status.SUCCESS && model != null) action(
        model,
        message,
        responseCode,
    )
    return this
}

inline fun <T : Any> ResultEmittedData<T>.onFailure(
    action: (
        model: Any?,
        title: String?,
        message: String?,
        responseCode: Int?,
        errorType: ErrorType?,
    ) -> Unit
): ResultEmittedData<T> {
    if (status == ResultEmittedData.Status.ERROR) action(
        model,
        title,
        message,
        responseCode,
        errorType
    )
    return this
}
