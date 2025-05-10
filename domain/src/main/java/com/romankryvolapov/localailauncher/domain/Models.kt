/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.domain

sealed class Models {

    enum class Engine {
        MLCEngine,
        MediaPipe,
        TensorFlow
    }

    sealed class MLCEngineModel(
        val modelName: String,
        val modelLib: String
    ) : Models() {
        object GEMMA_3_1B_QAT : MLCEngineModel(
            modelName = "gemma-3-1b-it-q4f16_1-MLC",
            modelLib = "gemma3_text_q4f16_1_15281663a194ab7a82d5f6c3b0d59432"
        )
    }

    sealed class MediaPipeModel(
        val modelName: String
    ) : Models() {
        object GEMMA_3_1B_QAT : MediaPipeModel(
            modelName = "gemma_3"
        )
    }

    sealed class TensorFlowModel(
        val modelName: String
    ) : Models() {
        object GEMMA_3_1B_QAT : TensorFlowModel(
            modelName = "gemma_3"
        )
    }

}