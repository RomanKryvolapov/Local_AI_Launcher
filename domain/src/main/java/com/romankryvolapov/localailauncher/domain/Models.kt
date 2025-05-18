/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.domain

import java.io.File

val models = mutableListOf(
//    LlamaCppModel(
//        modelID = "GEMMA_3_1B",
//        modelName = "Gemma 3 1B",
//        modelFileName = "llama/gemma-3-1B-it-QAT-Q4_0.gguf",
//    ),
    Model.LlamaCppModel(
        modelID = "GEMMA_3_4B",
        modelName = "Gemma 3 4B",
        file = File(
            externalFilesDirectory,
            "models/gguf/gemma-3-4B-it-QAT-Q4_0.gguf"
        ),
        fileUrl = "https://huggingface.co/lmstudio-community/gemma-3-4B-it-qat-GGUF/resolve/main/gemma-3-4B-it-QAT-Q4_0.gguf",
    ),
    Model.MLCEngineModel(
        modelID = "GEMMA_3_1B",
        modelName = "Gemma 3 1B",
        modelLib = "gemma3_text_q4f16_1_15281663a194ab7a82d5f6c3b0d59432",
        file = File(
            externalFilesDirectory,
            "models/bin/gemma-3-1b/gemma-3-1b-it-q4f16_1-MLC"
        ),
//        fileUrl = "https://huggingface.co/mlc-ai/gemma-3-1b-it-q4f16_1-MLC/tree/main",
    ),
    Model.MediaPipeModel(
        modelID = "GEMMA_3_1B",
        modelName = "Gemma 3 1B",
        file = File(
            externalFilesDirectory,
            "models/task/Gemma3-1B-IT_multi-prefill-seq_q4_ekv2048.task"
        ),
        fileUrl = "https://huggingface.co/litert-community/Gemma3-1B-IT/resolve/main/Gemma3-1B-IT_multi-prefill-seq_q4_ekv2048.task",
    ),
    Model.OnnxModel(
        modelID = "GEMMA_3_1B",
        modelName = "Gemma 3 1B",
        file = File(
            externalFilesDirectory,
            "models/onnx/gemma-3-1b"
        ),
        fileUrl = "https://huggingface.co/onnx-community/gemma-3-1b-it-ONNX-GQA/resolve/main/onnx/model_q4.onnx",
    ),
)