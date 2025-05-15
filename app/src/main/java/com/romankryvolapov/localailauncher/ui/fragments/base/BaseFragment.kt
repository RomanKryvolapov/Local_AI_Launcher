/**
 * Created & Copyright 2025 by Roman Kryvolapov
 *
 * Activity lifecycle:
 *
 * onCreate(savedInstanceState: Bundle?)
 * onStart()
 * onRestart()
 * onResume()
 *
 * onPause()
 * onStop()
 * onDestroy()
 *
 * Fragment lifecycle:
 *
 * onAttach(context: Context)
 * onCreate()
 * onCreateView()
 * onViewCreated()
 * onViewStateRestored(savedInstanceState: Bundle?)
 * onStart()
 * onResume()
 *
 * onPause()
 * onStop()
 * onDestroyView()
 * onDestroy()
 * onDetach()
 *
 * View lifecycle:
 *
 * onAttachedToWindow()
 * requestLayout()
 * measure()
 * onMeasure()
 * layout()
 * onLayout()
 * invalidate()
 * dispatchToDraw()
 * draw()
 * onDraw()
 *
 */
package com.romankryvolapov.localailauncher.ui.fragments.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ListAdapter
import android.widget.PopupWindow
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.ListPopupWindow
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.romankryvolapov.localailauncher.R
import com.romankryvolapov.localailauncher.data.extensions.getParcelableCompat
import com.romankryvolapov.localailauncher.common.models.common.LogUtil.logDebug
import com.romankryvolapov.localailauncher.common.models.common.LogUtil.logError
import com.romankryvolapov.localailauncher.extensions.findActivityNavController
import com.romankryvolapov.localailauncher.extensions.findParentFragmentByType
import com.romankryvolapov.localailauncher.extensions.findParentFragmentResultListenerFragmentManager
import com.romankryvolapov.localailauncher.extensions.hideKeyboard
import com.romankryvolapov.localailauncher.extensions.pxDimen
import com.romankryvolapov.localailauncher.extensions.setBackgroundColorResource
import com.romankryvolapov.localailauncher.extensions.showSpinner
import com.romankryvolapov.localailauncher.models.common.AlertDialogResult
import com.romankryvolapov.localailauncher.models.common.BannerMessage
import com.romankryvolapov.localailauncher.models.common.CardScanBottomSheetContent
import com.romankryvolapov.localailauncher.models.common.CardScanBottomSheetHolder
import com.romankryvolapov.localailauncher.models.common.DialogMessage
import com.romankryvolapov.localailauncher.models.common.ErrorState
import com.romankryvolapov.localailauncher.models.common.FullscreenLoadingState
import com.romankryvolapov.localailauncher.models.common.LoadingState
import com.romankryvolapov.localailauncher.models.common.MessageBannerHolder
import com.romankryvolapov.localailauncher.models.common.StringSource
import com.romankryvolapov.localailauncher.models.common.UiState
import com.romankryvolapov.localailauncher.models.list.CommonDialogWithSearchUi
import com.romankryvolapov.localailauncher.models.list.CommonSpinnerUi
import com.romankryvolapov.localailauncher.ui.BaseViewModel
import com.romankryvolapov.localailauncher.ui.activity.MainActivity
import com.romankryvolapov.localailauncher.ui.fragments.base.flow.BaseFlowFragment
import com.romankryvolapov.localailauncher.ui.fragments.common.search.CommonBottomSheetWithSearchFragment.Companion.COMMON_BOTTOM_SHEET_WITH_SEARCH_FRAGMENT_RESULT_BUNDLE_KEY
import com.romankryvolapov.localailauncher.ui.fragments.common.search.CommonBottomSheetWithSearchFragment.Companion.COMMON_BOTTOM_SHEET_WITH_SEARCH_FRAGMENT_RESULT_DATA_KEY
import com.romankryvolapov.localailauncher.ui.fragments.main.flow.MainTabsFlowFragment
import com.romankryvolapov.localailauncher.ui.view.ComplexGestureRefreshView
import com.romankryvolapov.localailauncher.utils.AlertDialogResultListener
import com.romankryvolapov.localailauncher.utils.SoftKeyboardStateWatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

abstract class BaseFragment<VB : ViewBinding, VM : BaseViewModel> : Fragment(),
    MessageBannerHolder,
    AlertDialogResultListener {

    companion object {
        private const val TAG = "BaseFragmentTag"
        const val DIALOG_EXIT = "DIALOG_EXIT"
    }

    abstract val viewModel: VM

    private var viewBinding: VB? = null

    private var popupWindow: PopupWindow? = null

    private var listPopupWindow: ListPopupWindow? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    val binding get() = viewBinding!!

    private var keyboardIsOpened = false
    private var keyboardOpenJob: Job? = null

    private var keyboardStateWatcher: SoftKeyboardStateWatcher? = null
    private var keyboardStateListener =
        object : SoftKeyboardStateWatcher.SoftKeyboardStateListener {
            override fun onSoftKeyboardOpened(keyboardHeight: Int) {
                keyboardOpenJob?.cancel()
                keyboardOpenJob = lifecycleScope.launch {
                    if (!keyboardIsOpened) {
                        keyboardIsOpened = true
                        onKeyboardStateChanged(true)
                    }
                }
            }

            override fun onSoftKeyboardClosed() {
                keyboardOpenJob?.cancel()
                keyboardOpenJob = lifecycleScope.launch {
                    if (keyboardIsOpened) {
                        keyboardIsOpened = false
                        onKeyboardStateChanged(false)
                    }
                }
            }
        }

    abstract fun getViewBinding(): VB

    // lifecycle

    final override fun onAttach(context: Context) {
        super.onAttach(context)
        logDebug("onAttach", TAG)
    }

    final override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        logDebug("onAttach", TAG)
    }

    final override fun onStart() {
        super.onStart()
        logDebug("onStart", TAG)
    }

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        logDebug("onCreateView", TAG)
        viewBinding = getViewBinding()
        return viewBinding?.root
    }

    final override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        logDebug("onViewCreated", TAG)
        setupNavControllers()
        subscribeToBaseViewModel()
        super.onViewCreated(view, savedInstanceState)
        viewModel.onViewCreated()
        (activity as? MainActivity)?.alertDialogResultListener = this
        setupKeyboardStateListener()
        onCreated()
        setupView()
        setupControls()
        parseArguments()
        subscribeToLiveData()
    }

    private fun setupKeyboardStateListener() {
        keyboardStateWatcher = SoftKeyboardStateWatcher(requireActivity())
        keyboardStateWatcher?.setStatusBarOffset(getStatusBarHeight())
    }

    private fun getStatusBarHeight(): Int {
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            context.pxDimen(resourceId)
        } else 0
    }

    final override fun onResume() {
        logDebug("onResume", TAG)
        super.onResume()
        keyboardStateWatcher?.addSoftKeyboardStateListener(keyboardStateListener)
        if (this !is BaseFlowFragment) {
            val callback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    onBackPressed()
                }
            }
            requireActivity().onBackPressedDispatcher.addCallback(this, callback)
        }
        onResumed()
        viewModel.fragmentOnResume()
        viewModel.onResumed()
    }

    final override fun onPause() {
        logDebug("onPause", TAG)
        super.onPause()
        hideSettingsMenu()
        onPaused()
        viewModel.onPaused()
        keyboardStateWatcher?.removeSoftKeyboardStateListener(keyboardStateListener)
        onKeyboardStateChanged(false)
    }

    final override fun onStop() {
        logDebug("onStop", TAG)
        super.onStop()
        onStopped()
        viewModel.onStopped()
    }

    final override fun onDestroyView() {
        logDebug("onDestroyView", TAG)
        viewBinding = null
        listPopupWindow?.dismiss()
        popupWindow?.dismiss()
        viewModel.unbindFlowNavController()
        viewModel.unbindActivityNavController()
        if (viewModel.mainTabsEnum == null) {
            viewModel.unbindTabNavController()
        }
        super.onDestroyView()
    }

    final override fun onDestroy() {
        logDebug("onDestroy", TAG)
        super.onDestroy()
        onDestroyed()
        viewModel.onDestroyed()
    }

    final override fun onDetach() {
        logDebug("onDetach", TAG)
        super.onDetach()
        onDetached()
        viewModel.onDetached()
    }

    final override fun onAlertDialogResultReady(result: AlertDialogResult) {
        logDebug("onAlertDialogResult", TAG)
        onAlertDialogResult(result)
        viewModel.onAlertDialogResult()
        viewModel.onAlertDialogResult(result)
    }

    protected open fun onCreated() {
        // Override when needed
    }

    protected open fun setupView() {
        // Override when needed
    }

    protected open fun setupControls() {
        // Override when needed
    }

    protected open fun subscribeToLiveData() {
        // Override when needed
    }

    open fun onAlertDialogResult(result: AlertDialogResult) {
        // Override when needed
    }

    protected open fun onResumed() {
        // Override when needed
    }

    protected open fun parseArguments() {
        // Override when needed
    }

    protected open fun onPaused() {
        // Override when needed
    }

    protected open fun onStopped() {
        // Override when needed
    }

    protected open fun onDestroyed() {
        // Override when needed
    }

    protected open fun onDetached() {
        // Override when needed
    }

    protected open fun onKeyboardStateChanged(isOpened: Boolean) {
        logDebug("onKeyboardStateChanged isOpened: $isOpened", TAG)
        // Override when needed
    }

    protected open fun setupNavControllers() {
        setupActivityNavController()
        viewModel.bindFlowNavController(findNavController())
        findParentFragmentByType(MainTabsFlowFragment::class.java)?.let {
            it.navHostFragmentMap[viewModel.mainTabsEnum?.menuID]?.get()?.navController?.let { navController ->
                viewModel.bindTabNavController(navController)
            }
        }
    }

    protected fun setupActivityNavController() {
        viewModel.bindActivityNavController(findActivityNavController())
    }

    private fun subscribeToBaseViewModel() {
        viewModel.closeActivityLiveData.observe(viewLifecycleOwner) {
            activity?.finish()
        }
        viewModel.backPressedFailedLiveData.observe(viewLifecycleOwner) {
            try {
                ((parentFragment as? NavHostFragment)?.parentFragment as? BaseFlowFragment<*, *>)?.onExit()
            } catch (e: Exception) {
                logError("backPressedFailedLiveData Exception: ${e.message}", e, TAG)
            }
        }
        viewModel.showBannerMessageLiveData.observe(viewLifecycleOwner) {
            showMessage(it)
        }
        viewModel.showDialogMessageLiveData.observe(viewLifecycleOwner) {
            showMessage(it)
        }
        viewModel.uiState.observe(viewLifecycleOwner) {
            when (it) {
                is UiState.Ready -> showReadyState()
                is UiState.Empty -> showEmptyState()
            }
        }
        viewModel.showLoadingDialogLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is FullscreenLoadingState.Loading -> (activity as? MessageBannerHolder)?.showFullscreenLoader(it.message)
                is FullscreenLoadingState.Ready -> (activity as? MessageBannerHolder)?.hideFullscreenLoader()
            }
        }
        viewModel.loadingState.observe(viewLifecycleOwner) {
            when (it) {
                is LoadingState.Ready -> hideLoader()
                is LoadingState.Loading -> showLoader(
                    message = it.message,
                    translucent = it.translucent,
                )
            }
        }
        viewModel.errorState.observe(viewLifecycleOwner) {
            when (it) {
                is ErrorState.Ready -> hideErrorState()
                is ErrorState.Error -> showErrorState(
                    title = it.title,
                    iconRes = it.iconRes,
                    showIcon = it.showIcon,
                    showTitle = it.showTitle,
                    description = it.description,
                    showDescription = it.showDescription,
                    showActionOneButton = it.showActionTwoButton,
                    showActionTwoButton = it.showActionTwoButton,
                    actionOneButtonText = it.actionOneButtonText,
                    actionTwoButtonText = it.actionTwoButtonText,
                )
            }
        }
    }

    final override fun showMessage(message: BannerMessage, anchorView: View?) {
        try {
            logDebug(
                "showMessage BannerMessage: ${message.message.getString(requireContext())}",
                TAG
            )
            (activity as? MessageBannerHolder)?.showMessage(message, anchorView)
        } catch (e: Exception) {
            logError("showMessage BannerMessage Exception: ${e.message}", e, TAG)
        }
    }

    final override fun showMessage(message: DialogMessage) {
        try {
            logDebug(
                "showMessage DialogMessage: ${message.message.getString(requireContext())}",
                TAG
            )
            (activity as? MessageBannerHolder)?.showMessage(message)
        } catch (e: Exception) {
            logError("showMessage DialogMessage Exception: ${e.message}", e, TAG)
        }
    }

    // hierarchy for view -> content, empty state, error state, loader

    fun showLoader(
        message: String? = null,
        translucent: Boolean = false,
    ) {
        try {
            val loaderView = view?.findViewById<FrameLayout>(R.id.loaderView)
            if (loaderView?.visibility != View.VISIBLE) {
                loaderView?.visibility = View.VISIBLE
            }
            if (!message.isNullOrEmpty() && loaderView?.visibility == View.VISIBLE) {
                logDebug("showLoader message: $message", TAG)
                val tvMessage = loaderView.findViewById<AppCompatTextView>(R.id.tvMessage)
                tvMessage?.text = message
            }
            val loaderLayout = view?.findViewById<FrameLayout>(R.id.loaderLayout)
            if (translucent) {
                loaderLayout?.setBackgroundColorResource(R.color.color_translucent)
            } else {
                loaderLayout?.setBackgroundColorResource(R.color.color_white)
            }
        } catch (e: Exception) {
            logError("showLoader Exception: ${e.message}", e, TAG)
        }
    }

    private fun hideLoader() {
        try {
            view?.findViewById<ComplexGestureRefreshView>(R.id.refreshLayout)?.isRefreshing = false
            val loaderView = view?.findViewById<FrameLayout>(R.id.loaderView)
            if (loaderView?.visibility != View.GONE) {
                loaderView?.visibility = View.GONE
            }
        } catch (e: Exception) {
            logError("hideLoader Exception: ${e.message}", e, TAG)
        }
    }

    override fun showFullscreenLoader(message: StringSource?) {
        (activity as MessageBannerHolder).showFullscreenLoader(message = message)
    }

    override fun hideFullscreenLoader() {
        (activity as MessageBannerHolder).hideFullscreenLoader()
    }

    protected fun showEmptyState() {
        logDebug("showEmptyState", TAG)
        try {
            val emptyStateView = view?.findViewById<FrameLayout>(R.id.emptyStateView)
            if (emptyStateView?.visibility != View.VISIBLE) {
                emptyStateView?.visibility = View.VISIBLE
            }
        } catch (e: Exception) {
            logError("showEmptyState Exception: ${e.message}", e, TAG)
        }
    }

    protected fun showReadyState() {
        logDebug("hideEmptyState", TAG)
        try {
            val emptyStateView = view?.findViewById<FrameLayout>(R.id.emptyStateView)
            if (emptyStateView?.visibility != View.GONE) {
                emptyStateView?.visibility = View.GONE
            }
        } catch (e: Exception) {
            logError("hideEmptyState Exception: ${e.message}", e, TAG)
        }
    }

    fun showErrorState(
        title: StringSource,
        description: StringSource,
        iconRes: Int? = null,
        showIcon: Boolean? = null,
        showTitle: Boolean? = null,
        showDescription: Boolean? = null,
        showActionOneButton: Boolean? = null,
        showActionTwoButton: Boolean? = null,
        actionOneButtonText: StringSource? = null,
        actionTwoButtonText: StringSource? = null,
    ) {
        logDebug("showErrorState", TAG)
        try {
            logDebug("showErrorState description: $description", TAG)
            val errorView = view?.findViewById<View>(R.id.errorView)
            val btnErrorActionOne = errorView?.findViewById<AppCompatButton>(R.id.btnErrorActionOne)
            val btnErrorActionTwo = errorView?.findViewById<AppCompatButton>(R.id.btnErrorActionTwo)
            val tvDescription =
                errorView?.findViewById<AppCompatTextView>(R.id.tvErrorViewDescription)
            val tvTitle =
                errorView?.findViewById<AppCompatTextView>(R.id.tvErrorViewTitle)
            val ivIcon =
                errorView?.findViewById<AppCompatImageView>(R.id.ivErrorIcon)
            errorView?.visibility = View.VISIBLE
            if (showTitle != null) {
                tvTitle?.isVisible = showTitle
            }
            if (showDescription != null) {
                tvDescription?.isVisible = showDescription
            }
            if (showActionOneButton != null) {
                btnErrorActionTwo?.isVisible = showActionOneButton
            }
            if (showActionTwoButton != null) {
                btnErrorActionTwo?.isVisible = showActionTwoButton
            }
            if (showIcon != null) {
                ivIcon?.isVisible = showIcon
            }
            tvTitle?.text = title.getString(requireContext())
            tvDescription?.text = description.getString(requireContext())
            if (actionOneButtonText != null) {
                btnErrorActionOne?.text = actionOneButtonText.getString(requireContext())
            }
            if (actionTwoButtonText != null) {
                btnErrorActionTwo?.text = actionTwoButtonText.getString(requireContext())
            }
            if (iconRes != null && iconRes != 0) {
                ivIcon?.setImageResource(iconRes)
            }
        } catch (e: Exception) {
            logError("showErrorState Exception: ${e.message}", e, TAG)
        }
    }

    private fun hideErrorState() {
        try {
            val errorView = view?.findViewById<FrameLayout>(R.id.errorView)
            errorView?.visibility = View.GONE
        } catch (e: Exception) {
            logError("hideErrorState Exception: ${e.message}", e, TAG)
        }
    }

    protected fun showSpinner(model: CommonSpinnerUi, anchor: View) {
        logDebug("showSpinner", TAG)
        hideKeyboard()
        if (model.list.isEmpty()) {
            showMessage(BannerMessage.error("List it empty"))
            return
        }
        listPopupWindow = anchor.showSpinner(
            model = model,
            clickListener = {
                viewModel.onSpinnerSelected(
                    model.copy(
                        selectedValue = it
                    )
                )
            }
        )
    }

    private fun measureContentWidth(context: Context, adapter: ListAdapter): Int {
        val measureParentViewGroup = FrameLayout(context)
        var itemView: View? = null
        var maxWidth = 0
        var itemType = 0
        val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        for (index in 0 until adapter.count) {
            val positionType = adapter.getItemViewType(index)
            if (positionType != itemType) {
                itemType = positionType
                itemView = null
            }
            itemView = adapter.getView(index, itemView, measureParentViewGroup)
            itemView.measure(widthMeasureSpec, heightMeasureSpec)
            val itemWidth = itemView.measuredWidth
            if (itemWidth > maxWidth) {
                maxWidth = itemWidth
            }
        }
        return maxWidth
    }

    protected fun setupDialogWithSearchResultListener() {
        logDebug("setupDialogWithSearchResultListener", TAG)
        findParentFragmentResultListenerFragmentManager()?.setFragmentResultListener(
            COMMON_BOTTOM_SHEET_WITH_SEARCH_FRAGMENT_RESULT_BUNDLE_KEY, viewLifecycleOwner
        ) { _, bundle ->
            bundle.getParcelableCompat<CommonDialogWithSearchUi>(
                COMMON_BOTTOM_SHEET_WITH_SEARCH_FRAGMENT_RESULT_DATA_KEY
            )?.let {
                viewModel.onDialogElementSelected(it)
            }
        }
    }

    open fun onBackPressed() {
        // Default on back pressed implementation for fragments.
        logDebug("onBackPressed", TAG)
        viewModel.onBackPressed()
    }

    fun showScanCardBottomSheet(content: CardScanBottomSheetContent) {
        (activity as? CardScanBottomSheetHolder)?.showCardBottomSheet(content = content)
    }


    private fun hideSettingsMenu() {
        popupWindow?.setOnDismissListener {
            popupWindow = null
        }
        popupWindow?.dismiss()
    }


}