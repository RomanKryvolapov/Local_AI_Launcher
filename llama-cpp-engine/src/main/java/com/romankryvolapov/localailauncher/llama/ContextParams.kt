package com.romankryvolapov.localailauncher.llama

data class ContextParams(
    val nCtx: Int? = null,
    val nThreads: Int? = null,
    val nThreadsBatch: Int? = null,
    val ropeFreqBase: Float? = null,
    val ropeFreqScale: Float? = null,
    val flashAttn: Boolean? = null,
    val embeddings: Boolean? = null
)

