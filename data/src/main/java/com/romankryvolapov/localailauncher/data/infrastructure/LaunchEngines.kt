/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.data.infrastructure

import ai.mlc.mlcllm.MLCEngine
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import org.tensorflow.lite.Interpreter

class LaunchEngines {

    var mlcEngine: MLCEngine? = null

    var interpreter: Interpreter? = null

    var llmInference: LlmInference? = null

}