/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.domain

val models = mutableListOf(
//    LlamaCppModel(
//        modelID = "GEMMA_3_1B",
//        modelName = "Gemma 3 1B",
//        modelFileName = "llama/gemma-3-1B-it-QAT-Q4_0.gguf",
//    ),
    Model.LlamaCppModel(
        modelID = "GEMMA_3_4B",
        modelName = "Gemma 3 4B",
        filePath = "gemma-3-4B-it-QAT-Q4_0.gguf",
        fileUrl = "https://huggingface.co/lmstudio-community/gemma-3-4B-it-qat-GGUF/resolve/main/gemma-3-4B-it-QAT-Q4_0.gguf",
    ),
    Model.MLCEngineModel(
        modelID = "GEMMA_3_1B",
        modelName = "Gemma 3 1B",
        modelLib = "gemma3_text_q4f16_1_15281663a194ab7a82d5f6c3b0d59432",
        filePath = "gemma-3-1b/gemma-3-1b-it-q4f16_1-MLC",
    ),
    Model.MediaPipeModel(
        modelID = "GEMMA_3_1B",
        modelName = "Gemma 3 1B",
        filePath = "Gemma3-1B-IT_multi-prefill-seq_q4_ekv2048.task",
    ),
    Model.OnnxModel(
        modelID = "GEMMA_3_1B",
        modelName = "Gemma 3 1B",
        filePath = "gemma-3-1b",
    ),
)