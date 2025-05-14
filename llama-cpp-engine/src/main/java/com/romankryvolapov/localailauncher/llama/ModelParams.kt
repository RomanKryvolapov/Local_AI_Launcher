package com.romankryvolapov.localailauncher.llama

data class ModelParams(
    val useMmap: Boolean? = null,
    val useMlock: Boolean? = null,
    val vocabOnly: Boolean? = null,
    val checkTensors: Boolean? = null,
    val nGpuLayers: Int? = null,
    val splitMode: Int? = null,
    val mainGpu: Int? = null
)
