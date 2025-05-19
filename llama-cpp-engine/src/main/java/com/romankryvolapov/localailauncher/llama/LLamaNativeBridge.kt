package com.romankryvolapov.localailauncher.llama

import com.romankryvolapov.localailauncher.llama.LLamaAndroid.Companion.IntVar

object LLamaNativeBridge {

    external fun log_to_android()
    external fun load_model(filename: String): Long
    external fun free_model(model: Long)
    external fun new_context(
        model: Long,
        contextSize: Int,
    ): Long

    external fun free_context(context: Long)
    external fun backend_init(numa: Boolean)
    external fun backend_free()
    external fun new_batch(
        nTokens: Int,
        embd: Int,
        nSeqMax: Int
    ): Long

    external fun free_batch(batch: Long)
    external fun new_sampler(): Long
    external fun free_sampler(sampler: Long)
    external fun bench_model(
        context: Long,
        model: Long,
        batch: Long,
        pp: Int,
        tg: Int,
        pl: Int,
        nr: Int
    ): String

    external fun system_info(): String

    external fun completion_init(
        context: Long,
        batch: Long,
        text: String,
        formatChat: Boolean,
        nLen: Int
    ): Int

    external fun completion_loop(
        context: Long,
        batch: Long,
        sampler: Long,
        nLen: Int,
        ncur: IntVar
    ): String?

    external fun kv_cache_clear(context: Long)

    external fun new_context_with_params(
        model: Long,
        contextSize: Int,
        nBatch: Int,
        nUbatch: Int,
        nSeqMax: Int,
        nThreads: Int,
        nThreadsBatch: Int,
        ropeFreqBase: Float,
        ropeFreqScale: Float,
        embeddings: Boolean,
        offloadKqv: Boolean,
        flashAttn: Boolean,
        noPerf: Boolean,
        opOffload: Boolean
    ): Long

    external fun new_sampler_with_params(
        temperature: Float,
        topK: Int,
        topP: Float,
    ): Long
}