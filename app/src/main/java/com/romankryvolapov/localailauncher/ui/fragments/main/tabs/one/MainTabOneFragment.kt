/**
 * Created & Copyright 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.ui.fragments.main.tabs.one

import com.romankryvolapov.localailauncher.databinding.FragmentMainTabOneBinding
import com.romankryvolapov.localailauncher.domain.utils.LogUtil.logDebug
import com.romankryvolapov.localailauncher.extensions.enableChangeAnimations
import com.romankryvolapov.localailauncher.extensions.onClickThrottle
import com.romankryvolapov.localailauncher.extensions.setTextColorResource
import com.romankryvolapov.localailauncher.extensions.showKeyboard
import com.romankryvolapov.localailauncher.models.chat.ChatMessageErrorUi
import com.romankryvolapov.localailauncher.models.chat.ChatMessageModelUi
import com.romankryvolapov.localailauncher.models.chat.ChatMessageUserUi
import com.romankryvolapov.localailauncher.models.common.AlertDialogResult
import com.romankryvolapov.localailauncher.ui.fragments.main.base.BaseMainTabFragment
import com.romankryvolapov.localailauncher.ui.fragments.main.tabs.one.MainTabOneViewModel.Companion.EngineGeneratingState
import com.romankryvolapov.localailauncher.ui.fragments.main.tabs.one.list.ChatMessagesAdapter
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.system.exitProcess

class MainTabOneFragment :
    BaseMainTabFragment<FragmentMainTabOneBinding, MainTabOneViewModel>(),
    ChatMessagesAdapter.ClickListener {

    companion object {
        private const val TAG = "MainTabOneFragmentTag"
    }

    override fun getViewBinding() = FragmentMainTabOneBinding.inflate(layoutInflater)
    override val viewModel: MainTabOneViewModel by viewModel()

    private val adapter: ChatMessagesAdapter by inject()

    override fun setupControls() {
        binding.recyclerView.adapter = adapter
        binding.recyclerView.enableChangeAnimations(false)
        adapter.clickListener = this
        binding.btnSend.onClickThrottle {
            if (viewModel.engineGeneratingStateLiveData.value == EngineGeneratingState.IN_PROCESS) {
                viewModel.cancelGeneration()
            } else {
                viewModel.sendMessage(binding.etSearch.text.toString())
                binding.etSearch.text?.clear()
            }
        }
        binding.etSearch.showKeyboard()
        binding.spinnerEngine.onClickThrottle {
            viewModel.onSpinnerEngineClicked()
        }
        binding.spinnerModel.onClickThrottle {
            viewModel.onSpinnerModelClicked()
        }
        binding.btnLoad.onClickThrottle {
            viewModel.onLoadClicked()
        }
    }

    override fun subscribeToLiveData() {
        viewModel.engineGeneratingStateLiveData.observe(viewLifecycleOwner) {
            binding.btnSend.text = it.message
            binding.btnSend.setTextColorResource(it.messageColourRes)
        }
        viewModel.engineLoadStateLiveData.observe(viewLifecycleOwner) {
            binding.btnLoad.text = it.message
            binding.btnLoad.setTextColorResource(it.messageColourRes)
        }
        viewModel.messagesLiveData.observe(viewLifecycleOwner) {
            logDebug("messagesLiveData size: ${it.size}", TAG)
            adapter.items = it
            binding.recyclerView.scrollToPosition(it.size)
        }
        viewModel.engineLiveData.observe(viewLifecycleOwner) {
            binding.spinnerEngine.text = "Engine (click to change): $it"
        }
        viewModel.modelLiveData.observe(viewLifecycleOwner) {
            binding.spinnerModel.text = "Model (click to change): $it"
        }
    }

    override fun onAlertDialogResult(result: AlertDialogResult) {
        if (result.messageId == DIALOG_EXIT && result.isPositive) {
            exitProcess(0)
        }
    }

    override fun onUserMessageClicked(model: ChatMessageUserUi) {

    }

    override fun onModelMessageClicked(model: ChatMessageModelUi) {

    }

    override fun onErrorMessageClicked(model: ChatMessageErrorUi) {

    }

}