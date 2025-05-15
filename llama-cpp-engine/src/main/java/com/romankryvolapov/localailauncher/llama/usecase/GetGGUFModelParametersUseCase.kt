/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.llama.usecase

import com.romankryvolapov.localailauncher.common.extensions.getEnumIntValue
import com.romankryvolapov.localailauncher.common.extensions.print
import com.romankryvolapov.localailauncher.common.models.common.ErrorType
import com.romankryvolapov.localailauncher.common.models.common.LogUtil.logDebug
import com.romankryvolapov.localailauncher.common.models.common.LogUtil.logError
import com.romankryvolapov.localailauncher.common.models.common.ResultEmittedData
import com.romankryvolapov.localailauncher.common.models.common.TypeEnumInt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

class GetGGUFModelParametersUseCase {

    companion object {
        private const val TAG = "GetGGUFModelParametersUseCaseTag"

        private const val MAGIC = 1179993927

        enum class GGUFType(override val type: Int) : TypeEnumInt {
            UINT8(0),
            INT8(1),
            UINT16(2),
            INT16(3),
            UINT32(4),
            INT32(5),
            FLOAT32(6),
            BOOL(7),
            STRING(8),
            ARRAY(9),
            UINT64(10),
            INT64(11),
            FLOAT64(12);
        }
    }

    fun invoke(
        modelFile: File
    ): Flow<ResultEmittedData<Map<String, String>>> = callbackFlow {
        logDebug("invoke", TAG)
        try {
            val result = mutableMapOf<String, String>()
            FileInputStream(modelFile).channel.use { channel ->

                val headerBuf = ByteBuffer.allocate(4 + 4 + 8 + 8).order(ByteOrder.LITTLE_ENDIAN)
                channel.read(headerBuf)
                headerBuf.flip()

                val magic = headerBuf.int
                if (magic != MAGIC) {
                    trySend(
                        ResultEmittedData.error(
                            model = null,
                            error = null,
                            title = "Error reading GGUF model",
                            responseCode = null,
                            message = "magic $magic != $MAGIC",
                            errorType = ErrorType.EXCEPTION
                        )
                    )
                    return@callbackFlow
                }

                val version = headerBuf.int
                val nTensors = headerBuf.long
                val nKv = headerBuf.long

                result["magic"] = magic.toString()
                result["version"] = version.toString()
                result["nTensors"] = nTensors.toString()
                result["nKv"] = nKv.toString()

                fun readValue(typeEnum: GGUFType): String? {
                    return when (typeEnum) {
                        GGUFType.UINT8 -> {
                            val buf = ByteBuffer.allocate(1)
                            channel.read(buf); buf.flip()
                            buf.get().toUByte().toString()
                        }

                        GGUFType.INT8 -> {
                            val buf = ByteBuffer.allocate(1)
                            channel.read(buf); buf.flip()
                            buf.get().toString()
                        }

                        GGUFType.UINT16 -> {
                            val buf = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN)
                            channel.read(buf); buf.flip()
                            buf.short.toUShort().toString()
                        }

                        GGUFType.INT16 -> {
                            val buf = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN)
                            channel.read(buf); buf.flip()
                            buf.short.toString()
                        }

                        GGUFType.UINT32 -> {
                            val buf = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN)
                            channel.read(buf); buf.flip()
                            buf.int.toUInt().toString()
                        }

                        GGUFType.INT32 -> {
                            val buf = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN)
                            channel.read(buf); buf.flip()
                            buf.int.toString()
                        }

                        GGUFType.FLOAT32 -> {
                            val buf = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN)
                            channel.read(buf); buf.flip()
                            buf.float.toString()
                        }

                        GGUFType.BOOL -> {
                            val buf = ByteBuffer.allocate(1)
                            channel.read(buf); buf.flip()
                            (buf.get().toInt() != 0).toString()
                        }

                        GGUFType.STRING -> {
                            val lenBuf = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN)
                            channel.read(lenBuf); lenBuf.flip()
                            val strLen = lenBuf.long.toInt()
                            val bytes = ByteArray(strLen)
                            channel.read(ByteBuffer.wrap(bytes))
                            String(bytes, Charsets.UTF_8)
                        }

                        GGUFType.ARRAY -> {
                            val elemTypeBuf = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN)
                            channel.read(elemTypeBuf); elemTypeBuf.flip()
                            val elemType = elemTypeBuf.int
                            val countBuf = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN)
                            channel.read(countBuf); countBuf.flip()
                            val count = countBuf.long.toInt()
                            val elemEnum = getEnumIntValue<GGUFType>(elemType) ?: return null
                            List(count) { readValue(elemEnum) }.joinToString(separator = ",")
                        }

                        GGUFType.UINT64 -> {
                            val buf = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN)
                            channel.read(buf); buf.flip()
                            buf.long.toULong().toString()
                        }

                        GGUFType.INT64 -> {
                            val buf = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN)
                            channel.read(buf); buf.flip()
                            buf.long.toString()
                        }

                        GGUFType.FLOAT64 -> {
                            val buf = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN)
                            channel.read(buf); buf.flip()
                            buf.double.toString()
                        }
                    }
                }

                repeat(nKv.toInt()) {
                    val lenBuf = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN)
                    channel.read(lenBuf); lenBuf.flip()
                    val keyLen = lenBuf.long.toInt()
                    val keyBytes = ByteArray(keyLen)
                    channel.read(ByteBuffer.wrap(keyBytes))
                    val key = String(keyBytes, Charsets.UTF_8)
                    val typeBuf = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN)
                    channel.read(typeBuf); typeBuf.flip()
                    val typeInt = typeBuf.int
                    val typeEnum = getEnumIntValue<GGUFType>(typeInt)
                    if (typeEnum == null) {
                        trySend(
                            ResultEmittedData.error(
                                model = null,
                                error = null,
                                title = "Error reading GGUF model",
                                responseCode = null,
                                message = "Unknown GGUF type: $typeInt",
                                errorType = ErrorType.EXCEPTION
                            )
                        )
                        return@callbackFlow
                    }
                    val value = readValue(typeEnum)
                    if (value == null) {
                        trySend(
                            ResultEmittedData.error(
                                model = null,
                                error = null,
                                title = "Error reading GGUF model",
                                responseCode = null,
                                message = "Unknown GGUF type: $typeInt",
                                errorType = ErrorType.EXCEPTION
                            )
                        )
                        return@callbackFlow
                    }
                    result[key] = value
                }
            }
            trySend(
                ResultEmittedData.success(
                    model = result,
                    message = null,
                    responseCode = null
                )
            )
            result.print(TAG)
            logDebug("ready", TAG)
        } catch (e: Exception) {
            logError("Exception parsing GGUF: ${e.message}", e, TAG)
            trySend(
                ResultEmittedData.error(
                    model = null,
                    error = e,
                    title = "Error reading GGUF model",
                    responseCode = null,
                    message = e.message,
                    errorType = ErrorType.EXCEPTION
                )
            )
        }
    }.flowOn(Dispatchers.IO)

}

