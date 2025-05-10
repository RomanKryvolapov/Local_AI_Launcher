/**
 * Created & Copyright 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.domain.usecase

import ai.mlc.mlcllm.MLCEngine
import com.romankryvolapov.localailauncher.domain.usecase.base.BaseUseCase
import org.koin.core.component.inject
import kotlin.getValue

class SaveChatMessageUseCase  : BaseUseCase {

    companion object {
        private const val TAG = "CopyAssetsToFileUseCaseTag"
    }

    private val engine: MLCEngine by inject()


}