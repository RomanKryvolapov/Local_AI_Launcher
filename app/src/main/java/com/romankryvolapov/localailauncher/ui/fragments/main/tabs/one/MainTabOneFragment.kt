/**
 * Created 2025 by Roman Kryvolapov
 **/
package com.romankryvolapov.localailauncher.ui.fragments.main.tabs.one

import com.google.android.material.internal.ViewUtils.showKeyboard
import com.romankryvolapov.localailauncher.databinding.FragmentMainTabOneBinding
import com.romankryvolapov.localailauncher.domain.utils.LogUtil.logDebug
import com.romankryvolapov.localailauncher.extensions.enableChangeAnimations
import com.romankryvolapov.localailauncher.extensions.onClickThrottle
import com.romankryvolapov.localailauncher.extensions.showKeyboard
import com.romankryvolapov.localailauncher.models.chat.ChatMessageErrorUi
import com.romankryvolapov.localailauncher.models.chat.ChatMessageModelUi
import com.romankryvolapov.localailauncher.models.chat.ChatMessageUserUi
import com.romankryvolapov.localailauncher.models.common.AlertDialogResult
import com.romankryvolapov.localailauncher.ui.fragments.main.base.BaseMainTabFragment
import com.romankryvolapov.localailauncher.ui.fragments.main.tabs.one.list.ChatMessagesAdapter
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue
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
            if (viewModel.isLoadingLiveData.value == true) {
                viewModel.cancelGeneration()
            } else {
                viewModel.sendMessage(binding.etSearch.text.toString())
                binding.etSearch.text?.clear()
            }
        }
        binding.etSearch.showKeyboard()
    }

    override fun subscribeToLiveData() {
        viewModel.isLoadingLiveData.observe(viewLifecycleOwner) {
            binding.btnSend.text = if (it) "STOP" else "Send"
        }
        viewModel.messagesLiveData.observe(viewLifecycleOwner) {
            logDebug("messagesLiveData size: ${it.size}", TAG)
            adapter.items = it
            binding.recyclerView.scrollToPosition(it.size)
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