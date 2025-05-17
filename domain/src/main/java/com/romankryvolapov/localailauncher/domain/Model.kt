/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.domain

sealed class Model(
    open val modelID: String,
    open val modelName: String,
    open val engineID: String,
    open val engineName: String,
    open val fileUrl: String?,
    open val filePath: String,
    open val fileFolder: String,
) {

    class MLCEngineModel(
        override val modelID: String,
        override val modelName: String,
        override val engineID: String = "MLC",
        override val engineName: String = "MLC",
        override val fileUrl: String? = null,
        override val filePath: String,
        override val fileFolder: String = "${externalFilesDirectory.absolutePath}/models/bin/",
        val modelLib: String,
    ) : Model(
        modelID = modelID,
        engineID = engineID,
        modelName = modelName,
        engineName = engineName,
        fileUrl = fileUrl,
        filePath = filePath,
        fileFolder = fileFolder,
    )

    class MediaPipeModel(
        override val modelID: String,
        override val modelName: String,
        override val engineID: String = "MEDIA_PIPE",
        override val engineName: String = "MediaPipe",
        override val fileUrl: String? = null,
        override val filePath: String,
        override val fileFolder: String = "${externalFilesDirectory.absolutePath}/models/task/",
    ) : Model(
        modelID = modelID,
        engineID = engineID,
        modelName = modelName,
        engineName = engineName,
        fileUrl = fileUrl,
        filePath = filePath,
        fileFolder = fileFolder,
    )

    class OnnxModel(
        override val modelID: String,
        override val modelName: String,
        override val engineID: String = "ONNX",
        override val engineName: String = "ONNX",
        override val fileUrl: String? = null,
        override val filePath: String,
        override val fileFolder: String = "${externalFilesDirectory.absolutePath}/models/onnx/",
    ) : Model(
        modelID = modelID,
        engineID = engineID,
        modelName = modelName,
        engineName = engineName,
        fileUrl = fileUrl,
        filePath = filePath,
        fileFolder = fileFolder,
    )


    class LlamaCppModel(
        override val modelID: String,
        override val modelName: String,
        override val engineID: String = "LLAMA_CPP",
        override val engineName: String = "LLama.cpp",
        override val fileUrl: String? = null,
        override val filePath: String,
        override val fileFolder: String = "${externalFilesDirectory.absolutePath}/models/gguf/",
    ) : Model(
        modelID = modelID,
        engineID = engineID,
        modelName = modelName,
        engineName = engineName,
        fileUrl = fileUrl,
        filePath = filePath,
        fileFolder = fileFolder,
    )

}