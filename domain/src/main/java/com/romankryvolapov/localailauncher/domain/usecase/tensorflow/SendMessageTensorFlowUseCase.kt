/**
 * Created & Copyright 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.domain.usecase.tensorflow

import com.romankryvolapov.localailauncher.domain.models.ChatMessageModel
import com.romankryvolapov.localailauncher.domain.models.base.ErrorType
import com.romankryvolapov.localailauncher.domain.models.base.ResultEmittedData
import com.romankryvolapov.localailauncher.domain.usecase.base.BaseUseCase
import com.romankryvolapov.localailauncher.domain.utils.LogUtil.logDebug
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID

class SendMessageTensorFlowUseCase : BaseUseCase {

    companion object {
        private const val TAG = "SendMessageTensorFlowUseCaseTag"
    }

    fun invoke(
        message: String,
        dialogID: UUID,
        messageID: UUID,
        interpreter: Interpreter,
    ): Flow<ResultEmittedData<ChatMessageModel>> = flow {
        logDebug("invoke", TAG)
        emit(ResultEmittedData.loading())
        try {
            val byteBuffer = ByteBuffer
                .allocateDirect(message.length * 2)
                .order(ByteOrder.nativeOrder())
            for (char in message) {
                byteBuffer.putChar(char)
            }
            byteBuffer.rewind()
            val outputShape = interpreter.getOutputTensor(0).shape()
            val outputSize = outputShape.reduce { acc, dim -> acc * dim }
            val outputBuffer = FloatArray(outputSize)
            interpreter.run(byteBuffer, outputBuffer)
            val resultMessage = outputBuffer.joinToString(separator = ",")
            val chatMessage = ChatMessageModel(
                id = messageID,
                dialogID = dialogID,
                timeStamp = System.currentTimeMillis(),
                message = resultMessage,
                messageData = outputBuffer.joinToString()
            )
            emit(ResultEmittedData.success(
                model = chatMessage,
                message = null,
                responseCode = null
            ))
        } catch (e: Exception) {
            emit(ResultEmittedData.error(
                model = null,
                error = null,
                title = "TensorFlow error",
                responseCode = null,
                message = e.message,
                errorType = ErrorType.EXCEPTION
            ))
        }
    }


}