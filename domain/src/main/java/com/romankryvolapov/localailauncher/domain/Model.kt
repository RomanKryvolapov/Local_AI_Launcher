/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.domain

sealed class Model(
    open val modelID: String,
    open val engineID: String,
    open val modelName: String,
    open val engineName: String,
    open val filePath: String,
) {

    class MLCEngineModel(
        override val modelID: String,
        override val modelName: String,
        override val engineID: String = "MLC",
        override val engineName: String = "MLC",
        override val filePath: String,
        val modelLib: String,
    ) : Model(
        modelID = modelID,
        engineID = engineID,
        modelName = modelName,
        engineName = engineName,
        filePath = filePath,
    )

    class MediaPipeModel(
        override val modelID: String,
        override val modelName: String,
        override val engineID: String = "MEDIA_PIPE",
        override val engineName: String = "MediaPipe",
        override val filePath: String,
    ) : Model(
        modelID = modelID,
        engineID = engineID,
        modelName = modelName,
        engineName = engineName,
        filePath = filePath,
    )

    class OnnxModel(
        override val modelID: String,
        override val modelName: String,
        override val engineID: String = "ONNX",
        override val engineName: String = "ONNX",
        override val filePath: String,
    ) : Model(
        modelID = modelID,
        engineID = engineID,
        modelName = modelName,
        engineName = engineName,
        filePath = filePath,
    )


    class LlamaCppModel(
        override val modelID: String,
        override val modelName: String,
        override val engineID: String = "LLAMA_CPP",
        override val engineName: String = "LLama.cpp",
        override val filePath: String,
    ) : Model(
        modelID = modelID,
        engineID = engineID,
        modelName = modelName,
        engineName = engineName,
        filePath = filePath,
    )

}