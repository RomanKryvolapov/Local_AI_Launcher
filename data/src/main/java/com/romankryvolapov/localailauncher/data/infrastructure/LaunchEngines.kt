/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.data.infrastructure

import ai.mlc.mlcllm.MLCEngine
import ai.onnxruntime.genai.SimpleGenAI
import com.google.mediapipe.tasks.genai.llminference.LlmInference

class LaunchEngines {

    var mlcEngine: MLCEngine? = null

    var llmInference: LlmInference? = null

    var simpleGenAI: SimpleGenAI? = null

    fun clear() {

        mlcEngine?.reset()
        mlcEngine?.unload()
        mlcEngine = null

        llmInference?.close()
        llmInference = null

        simpleGenAI?.close()
        simpleGenAI = null

    }

}