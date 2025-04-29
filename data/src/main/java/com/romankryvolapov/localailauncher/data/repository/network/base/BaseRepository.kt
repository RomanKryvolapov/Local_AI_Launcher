/**
 * Created 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.data.repository.network.base

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.romankryvolapov.localailauncher.data.models.network.base.EmptyResponse
import com.romankryvolapov.localailauncher.data.models.network.base.ErrorApiResponse
import com.romankryvolapov.localailauncher.data.models.network.base.ErrorResponse
import com.romankryvolapov.localailauncher.data.models.network.base.getEmptyResponse
import com.romankryvolapov.localailauncher.data.utils.CoroutineContextProvider
import com.romankryvolapov.localailauncher.domain.models.base.ErrorType
import com.romankryvolapov.localailauncher.domain.models.base.ResultEmittedData
import com.romankryvolapov.localailauncher.domain.utils.LogUtil.logDebug
import com.romankryvolapov.localailauncher.domain.utils.LogUtil.logError
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import retrofit2.Response
import java.net.UnknownHostException
import javax.net.ssl.SSLPeerUnverifiedException

abstract class BaseRepository : KoinComponent {

    companion object {
        private const val TAG = "BaseRepositoryTag"
    }

    private val gson: Gson by inject()
    private val contextProvider: CoroutineContextProvider by inject()

    protected suspend fun <T> getResult(
        call: suspend () -> Response<T>,
    ): ResultEmittedData<T> {
        return getResultLoop(call)
    }

    private suspend fun <T> getResultLoop(
        call: suspend () -> Response<T>,
        retryCount: Int = 0,
    ): ResultEmittedData<T> {
        return try {
            val response = call()
            val responseCode = response.code()
            val successCode = when (responseCode) {
                200,
                201,
                202,
                203,
                204,
                205,
                206,
                207,
                208,
                226 -> true

                else -> false
            }
            val responseMessage = response.message()
            val responseBody = response.body() ?: getEmptyResponse()
            when {
                successCode && responseBody !is EmptyResponse -> {
                    dataSuccess(
                        model = responseBody,
                        message = responseMessage,
                        responseCode = responseCode,
                    )
                }

                successCode -> {
                    logDebug("getResult successCode", TAG)
                    dataSuccess(
                        model = getEmptyResponse(),
                        message = responseMessage,
                        responseCode = responseCode,
                    )
                }

                responseCode == 401 -> {
                    val errorApiResponse = parseErrorApi(response)
                    val message = when {
                        errorApiResponse != null && !errorApiResponse.detail.isNullOrEmpty() ->
                            buildErrorMassageFromApi(errorApiResponse)

                        !responseMessage.isNullOrEmpty() -> responseMessage
                        else -> "Error requesting authorizations, error code: $responseCode"
                    }
                    logError(
                        "getResult responseCode == 401, message: $message",
                        TAG
                    )
                    dataError(
                        message = message,
                        model = responseBody,
                        error = errorApiResponse,
                        responseCode = responseCode,
                        title = "Authorization error",
                        errorType = ErrorType.AUTHORIZATION,
                    )
                }

                else -> {
                    val errorApiResponse = parseErrorApi(response)
                    val title = when {
                        errorApiResponse != null && !errorApiResponse.title.isNullOrEmpty() -> errorApiResponse.title
                        else -> "Server error"
                    }
                    val message = when {
                        errorApiResponse != null && !errorApiResponse.detail.isNullOrEmpty() ->
                            buildErrorMassageFromApi(errorApiResponse)

                        !responseMessage.isNullOrEmpty() -> responseMessage
                        else -> "Error while receiving data from server, error code: $responseCode"
                    }
                    logError("getResult conditions else errorApiResponse: $errorApiResponse", TAG)
                    dataError(
                        title = title,
                        message = message,
                        model = responseBody,
                        error = errorApiResponse,
                        responseCode = responseCode,
                        errorType = ErrorType.SERVER_ERROR,
                    )
                }
            }
        } catch (exception: UnknownHostException) {
            logError(
                "getResult Exception is UnknownHostException, message: ${exception.message} stackTrace: ${exception.stackTrace}",
                exception,
                TAG
            )
            dataError(
                model = null,
                error = null,
                responseCode = null,
                title = "No internet connection",
                errorType = ErrorType.NO_INTERNET_CONNECTION,
                message = "Connect to the Internet and try again",
            )
        } catch (exception: SSLPeerUnverifiedException) {
            logError(
                "getResult Exception is SSLPeerUnverifiedException, message: ${exception.message} stackTrace: ${exception.stackTrace}\",",
                exception,
                TAG
            )
            dataError(
                model = null,
                error = null,
                responseCode = null,
                title = "Encryption error",
                errorType = ErrorType.EXCEPTION,
                message = "Encryption error when receiving data from the server, contact your service provider",
            )
        } catch (exception: JsonSyntaxException) {
            logError(
                "getResult Exception is JsonSyntaxException, message: ${exception.message} stackTrace: ${exception.stackTrace}\",",
                exception,
                TAG
            )
            dataError(
                model = null,
                error = null,
                responseCode = null,
                title = "Server error",
                errorType = ErrorType.EXCEPTION,
                message = "Error while receiving data from server, data format incorrect",
            )
        } catch (exception: java.io.EOFException) {
            logError(
                "getResult Exception is EOFException, message: ${exception.message} stackTrace: ${exception.stackTrace}\",",
                exception,
                TAG
            )
            dataError(
                model = null,
                error = null,
                responseCode = null,
                title = "Server error",
                errorType = ErrorType.EXCEPTION,
                message = exception.message ?: exception.toString(),
            )
        } catch (exception: Throwable) {
            logError(
                "getResult Exception is other, message: ${exception.message} stackTrace: ${exception.stackTrace}\",",
                exception,
                TAG
            )
            dataError(
                model = null,
                error = null,
                responseCode = null,
                title = "Server error",
                errorType = ErrorType.EXCEPTION,
                message = exception.message ?: exception.toString(),
            )
        }
    }

    private fun <T> parseErrorApi(response: Response<T>): ErrorApiResponse? {
        return try {
            val responseBodyString = response.errorBody()?.string()
            gson.fromJson(responseBodyString, ErrorApiResponse::class.java)
        } catch (e: Exception) {
            logError("parseErrorApi Exception: ${e.message}", e, TAG)
            null
        }
    }

    private fun buildErrorMassageFromApi(from: ErrorApiResponse): String {
        return buildString {
            if (from.status != null) {
                append("Error code: ")
                append(from.status)
                append("\n\n")
            }
            if (!from.detail.isNullOrEmpty()) {
                append("Message: ")
                append(from.detail)
            }
        }
    }


    private fun <T> dataError(
        model: T?,
        error: ErrorResponse?,
        responseCode: Int?,
        title: String?,
        message: String?,
        errorType: ErrorType?,
    ): ResultEmittedData<T> = ResultEmittedData.error(
        model = model,
        error = error,
        title = title,
        message = message,
        errorType = errorType,
        responseCode = responseCode,
    )

    private fun <T> dataSuccess(
        model: T,
        message: String?,
        responseCode: Int,
    ): ResultEmittedData<T> = ResultEmittedData.success(
        model = model,
        message = message,
        responseCode = responseCode,
    )

    /**
     * Use this when you want do something async
     */
    protected suspend fun doAsync(
        asyncMethod: suspend () -> Unit
    ) = withContext(contextProvider.io) {
        asyncMethod()
    }
}