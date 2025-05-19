package com.romankryvolapov.localailauncher.llama

import android.util.Log
import com.romankryvolapov.localailauncher.common.models.common.LogUtil.logDebug
import com.romankryvolapov.localailauncher.llama.LLamaNativeBridge.backend_init
import com.romankryvolapov.localailauncher.llama.LLamaNativeBridge.bench_model
import com.romankryvolapov.localailauncher.llama.LLamaNativeBridge.completion_init
import com.romankryvolapov.localailauncher.llama.LLamaNativeBridge.completion_loop
import com.romankryvolapov.localailauncher.llama.LLamaNativeBridge.free_batch
import com.romankryvolapov.localailauncher.llama.LLamaNativeBridge.free_context
import com.romankryvolapov.localailauncher.llama.LLamaNativeBridge.free_model
import com.romankryvolapov.localailauncher.llama.LLamaNativeBridge.free_sampler
import com.romankryvolapov.localailauncher.llama.LLamaNativeBridge.kv_cache_clear
import com.romankryvolapov.localailauncher.llama.LLamaNativeBridge.load_model
import com.romankryvolapov.localailauncher.llama.LLamaNativeBridge.log_to_android
import com.romankryvolapov.localailauncher.llama.LLamaNativeBridge.new_batch
import com.romankryvolapov.localailauncher.llama.LLamaNativeBridge.new_context
import com.romankryvolapov.localailauncher.llama.LLamaNativeBridge.new_sampler_with_params
import com.romankryvolapov.localailauncher.llama.LLamaNativeBridge.system_info
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors
import kotlin.concurrent.thread

class LLamaAndroid {

    companion object {

        private const val TAG = "LLamaAndroidTag"

        class IntVar(value: Int) {
            @Volatile
            var value: Int = value
                private set

            fun inc() {
                synchronized(this) {
                    value += 1
                }
            }
        }

        private sealed interface State {
            data object Idle : State
            data class Loaded(
                val model: Long,
                val context: Long,
                val batch: Long,
                val sampler: Long
            ) : State
        }

    }

    private val threadLocalState: ThreadLocal<State> = ThreadLocal.withInitial { State.Idle }

    private val runLoop: CoroutineDispatcher = Executors.newSingleThreadExecutor {
        thread(start = false, name = "Llm-RunLoop") {
            Log.d(TAG, "Dedicated thread for native code: ${Thread.currentThread().name}")

            // No-op if called more than once.
            System.loadLibrary("llama-cpp-engine")

            // Set llama log handler to Android
            log_to_android()
            backend_init(false)

            Log.d(TAG, system_info())

            it.run()
        }.apply {
            uncaughtExceptionHandler = Thread.UncaughtExceptionHandler { _, exception: Throwable ->
                Log.e(TAG, "Unhandled exception", exception)
            }
        }
    }.asCoroutineDispatcher()

    suspend fun bench(pp: Int, tg: Int, pl: Int, nr: Int = 1): String {
        return withContext(runLoop) {
            when (val state = threadLocalState.get()) {
                is State.Loaded -> {
                    Log.d(TAG, "bench(): $state")
                    bench_model(
                        state.context,
                        state.model,
                        state.batch,
                        pp,
                        tg,
                        pl,
                        nr
                    )
                }

                else -> throw IllegalStateException("No model loaded")
            }
        }
    }

    suspend fun load(
        pathToModel: String,
        temperature: Float,
        topK: Int,
        topP: Float,
        nTokens: Int,
        embd: Int,
        nSeqMax: Int,
        contextSize: Int,
    ) {
        withContext(runLoop) {
            when (threadLocalState.get()) {
                is State.Idle -> {

                    val model = load_model(pathToModel)

                    if (model == 0L) throw IllegalStateException("load_model() failed")

                    val context = new_context(
                        model = model,
                        contextSize = contextSize,
                    )
                    if (context == 0L) throw IllegalStateException("new_context() failed")

                    val batch = new_batch(
                        nTokens = nTokens,
                        embd = embd,
                        nSeqMax = nSeqMax
                    )

                    if (batch == 0L) throw IllegalStateException("new_batch() failed")

                    val sampler = new_sampler_with_params(
                        temperature = temperature,
                        topK = topK,
                        topP = topP,
                    )
                    if (sampler == 0L) throw IllegalStateException("new_sampler() failed")

                    Log.i(TAG, "Loaded model $pathToModel")
                    threadLocalState.set(State.Loaded(model, context, batch, sampler))
                }

                else -> throw IllegalStateException("Model already loaded")
            }
        }
    }

    fun send(
        message: String,
        prompt: String,
        template: String,
        nLen: Int,
        clearContextAfterAnswer: Boolean,
    ): Flow<String> = flow {
//        val messageWithPrompt = """
//        <|system|>$prompt<|user|>$message<|assistant|>
//        """.trimIndent()
        val messageWithPrompt = template
            .replace("prompt_text", prompt)
            .replace("message_text", message)
        logDebug("send messageWithPrompt: $messageWithPrompt", TAG)
        when (val state = threadLocalState.get()) {
            is State.Loaded -> {
                val ncur = IntVar(
                    completion_init(
                        context = state.context,
                        batch = state.batch,
                        text = messageWithPrompt,
                        formatChat = true,
                        nLen = nLen
                    )
                )
                while (ncur.value <= nLen) {
                    val str = completion_loop(
                        context = state.context,
                        batch = state.batch,
                        sampler = state.sampler,
                        nLen = nLen,
                        ncur = ncur
                    )
                    if (str == null) {
                        break
                    }
                    emit(str)
                }
                if (clearContextAfterAnswer) {
                    kv_cache_clear(state.context)
                }
            }

            else -> {}
        }
    }.flowOn(runLoop)

    /**
     * Unloads the model and frees resources.
     *
     * This is a no-op if there's no model loaded.
     */
    suspend fun unload() {
        withContext(runLoop) {
            when (val state = threadLocalState.get()) {
                is State.Loaded -> {
                    free_context(state.context)
                    free_model(state.model)
                    free_batch(state.batch)
                    free_sampler(state.sampler);

                    threadLocalState.set(State.Idle)
                }

                else -> {}
            }
        }
    }


}
