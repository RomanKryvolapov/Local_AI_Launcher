package com.romankryvolapov.localailauncher.mediapipe

import com.google.mediapipe.tasks.genai.llminference.LlmInference

var engine: LlmInference? = null

fun clear() {
    engine?.close()
    engine = null
}

