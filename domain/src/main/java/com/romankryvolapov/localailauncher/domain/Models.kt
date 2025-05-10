/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.domain

import com.romankryvolapov.localailauncher.domain.Model.MLCEngineModel
import com.romankryvolapov.localailauncher.domain.Model.MediaPipeModel

val models = mutableListOf<Model>(
    MLCEngineModel(
        modelID = "GEMMA_3_1B",
        modelName = "Gemma 3 1B",
        modelFileName = "gemma-3-1b-it-q4f16_1-MLC",
        modelLib = "gemma3_text_q4f16_1_15281663a194ab7a82d5f6c3b0d59432",
    ),
    MediaPipeModel(
        modelID = "GEMMA_3_1B",
        modelName = "Gemma 3 1B",
        modelFileName = "Gemma3-1B-IT_multi-prefill-seq_q4_ekv2048.task",
    ),
)