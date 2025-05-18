/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.domain

import java.io.File

sealed class Model(
    open val modelID: String,
    open val modelName: String,
    open val engineID: String,
    open val engineName: String,
    open val fileUrl: String?,
    open val file: File,
    open val isNeedAuthorization: Boolean,
) {

    class MLCEngineModel(
        override val modelID: String,
        override val modelName: String,
        override val engineID: String = "MLC",
        override val engineName: String = "MLC",
        override val fileUrl: String? = null,
        override val file: File,
        override val isNeedAuthorization: Boolean = false,
        val modelLib: String,
    ) : Model(
        modelID = modelID,
        engineID = engineID,
        modelName = modelName,
        engineName = engineName,
        fileUrl = fileUrl,
        file = file,
        isNeedAuthorization = isNeedAuthorization,
    )

    class MediaPipeModel(
        override val modelID: String,
        override val modelName: String,
        override val engineID: String = "MEDIA_PIPE",
        override val engineName: String = "MediaPipe",
        override val fileUrl: String? = null,
        override val isNeedAuthorization: Boolean = true,
        override val file: File,
    ) : Model(
        modelID = modelID,
        engineID = engineID,
        modelName = modelName,
        engineName = engineName,
        fileUrl = fileUrl,
        file = file,
        isNeedAuthorization = isNeedAuthorization,
    )

    class OnnxModel(
        override val modelID: String,
        override val modelName: String,
        override val engineID: String = "ONNX",
        override val engineName: String = "ONNX",
        override val fileUrl: String? = null,
        override val isNeedAuthorization: Boolean = false,
        override val file: File,
    ) : Model(
        modelID = modelID,
        engineID = engineID,
        modelName = modelName,
        engineName = engineName,
        fileUrl = fileUrl,
        file = file,
        isNeedAuthorization = isNeedAuthorization,
    )


    class LlamaCppModel(
        override val modelID: String,
        override val modelName: String,
        override val engineID: String = "LLAMA_CPP",
        override val engineName: String = "LLama.cpp",
        override val fileUrl: String? = null,
        override val isNeedAuthorization: Boolean = false,
        override val file: File,
    ) : Model(
        modelID = modelID,
        engineID = engineID,
        modelName = modelName,
        engineName = engineName,
        fileUrl = fileUrl,
        file = file,
        isNeedAuthorization = isNeedAuthorization,
    )

}