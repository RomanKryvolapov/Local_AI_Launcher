/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.data.infrastructure

import ai.mlc.mlcllm.MLCEngine
import com.google.mediapipe.tasks.genai.llminference.LlmInference

class LaunchEngines {

    var mlcEngine: MLCEngine? = null

    var llmInference: LlmInference? = null

}