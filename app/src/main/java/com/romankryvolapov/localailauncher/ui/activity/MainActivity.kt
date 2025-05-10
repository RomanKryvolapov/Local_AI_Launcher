/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.ui.activity

import android.Manifest
import android.app.Dialog
import android.content.ComponentCallbacks2
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.annotation.CallSuper
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.fragment.NavHostFragment
import com.romankryvolapov.localailauncher.BuildConfig
import com.romankryvolapov.localailauncher.R
import com.romankryvolapov.localailauncher.databinding.ActivityMainBinding
import com.romankryvolapov.localailauncher.domain.utils.LogUtil.logDebug
import com.romankryvolapov.localailauncher.domain.utils.LogUtil.logError
import com.romankryvolapov.localailauncher.extensions.hideKeyboard
import com.romankryvolapov.localailauncher.extensions.makeStatusBarTransparent
import com.romankryvolapov.localailauncher.models.common.AlertDialogResult
import com.romankryvolapov.localailauncher.models.common.BannerMessage
import com.romankryvolapov.localailauncher.models.common.CardScanBottomSheetContent
import com.romankryvolapov.localailauncher.models.common.CardScanBottomSheetHolder
import com.romankryvolapov.localailauncher.models.common.DialogMessage
import com.romankryvolapov.localailauncher.models.common.MessageBannerHolder
import com.romankryvolapov.localailauncher.models.common.StartDestination
import com.romankryvolapov.localailauncher.models.common.StringSource
import com.romankryvolapov.localailauncher.ui.view.FullscreenLoaderView
import com.romankryvolapov.localailauncher.utils.AlertDialogResultListener
import com.romankryvolapov.localailauncher.utils.AppUncaughtExceptionHandler
import com.romankryvolapov.localailauncher.utils.BannerMessageWindowManager
import com.romankryvolapov.localailauncher.utils.CurrentContext
import com.romankryvolapov.localailauncher.utils.InactivityTimer
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(),
    MessageBannerHolder,
    CardScanBottomSheetHolder,
    ComponentCallbacks2 {

    companion object {
        private const val TAG = "BaseActivityTag"
    }

    private val viewModel: MainViewModel by viewModel()
    private val appContext: Context by inject()
    private val inactivityTimer: InactivityTimer by inject()
    private val currentContext: CurrentContext by inject()

    private fun getStartDestination(): StartDestination {
        return viewModel.getStartDestination(intent)
    }

    lateinit var binding: ActivityMainBinding

    private lateinit var bannerMessageWindowManager: BannerMessageWindowManager

    var alertDialogResultListener: AlertDialogResultListener? = null

    private var fullscreenLoaderView: FullscreenLoaderView? = null

    private var messageDialog: Dialog? = null

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        Thread.setDefaultUncaughtExceptionHandler(AppUncaughtExceptionHandler())
        currentContext.attachBaseContext(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bannerMessageWindowManager = BannerMessageWindowManager(this)
        setupNavController()
        subscribeToLiveData()
        viewModel.onViewCreated()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        if (BuildConfig.DEBUG && Build.VERSION.SDK_INT <= 29 &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                100
            )
        }


        // TODO uncomment for disable screenshots
        //        window.setFlags(
        //            WindowManager.LayoutParams.FLAG_SECURE,
        //            WindowManager.LayoutParams.FLAG_SECURE
        //        )
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            viewModel.dispatchTouchEvent()
        }
        return super.dispatchTouchEvent(event)
    }

    override fun showCardBottomSheet(content: CardScanBottomSheetContent) {
//        ScanCardBottomSheetFragment.newInstance(content = content).also { bottomSheet ->
//            bottomSheet.show(supportFragmentManager, "ScanCardBottomSheetFragmentTag")
//        }
    }

    private fun setupNavController() {
        val host =
            supportFragmentManager.findFragmentById(R.id.navigationContainer) as NavHostFragment
        try {
            // Try to get the current graph, if it is there, nav controller is valid.
            // When there is no graph, it throws IllegalStateException,
            // then we need to create a graph ourselves
            host.navController.graph
        } catch (e: Exception) {
            val graphInflater = host.navController.navInflater
            val graph = graphInflater.inflate(R.navigation.nav_activity)
            val startDestination = getStartDestination()
            graph.setStartDestination(startDestination.destination)
            host.navController.setGraph(graph, startDestination.arguments)
        }
        viewModel.bindActivityNavController(host.navController)
    }

    private fun subscribeToLiveData() {
        viewModel.closeActivityLiveData.observe(this) {
            finish()
        }
        viewModel.showBannerMessageLiveData.observe(this) {
            showMessage(it)
        }
        viewModel.showDialogMessageLiveData.observe(this) {
            showMessage(it)
        }
        inactivityTimer.lockStatusLiveData.observe(this) {
            if (it) {
                logDebug("lockStatusLiveData onLoginTimerExpired", TAG)
                messageDialog?.dismiss()
                viewModel.toLoginFragment()
            }
        }
    }


    override fun showMessage(message: BannerMessage, anchorView: View?) {
        logDebug("showMessage message: ${message.message.getString(this)}", TAG)
        try {
            bannerMessageWindowManager.showMessage(
                bannerMessage = message,
                anchorView = anchorView ?: binding.rootLayout,
            )
        } catch (e: Exception) {
            logError("showBannerMessage Exception: ${e.message}", e, TAG)
        }
    }

    override fun showMessage(message: DialogMessage) {
        val builder = AlertDialog.Builder(this)
            .setMessage(message.message.getString(this))
        if (message.title != null) {
            builder.setTitle(message.title.getString(this))
        }
        if (message.positiveButtonText != null) {
            builder.setPositiveButton(message.positiveButtonText.getString(this)) { dialog, _ ->
                logDebug("alertDialog result positive", TAG)
                dialog.cancel()
                alertDialogResultListener?.onAlertDialogResultReady(
                    AlertDialogResult(
                        messageId = message.messageID,
                        isPositive = true,
                    )
                )
            }
        }
        if (message.negativeButtonText != null) {
            builder.setNegativeButton(message.negativeButtonText.getString(this)) { dialog, _ ->
                logDebug("alertDialog result negative", TAG)
                dialog.cancel()
                alertDialogResultListener?.onAlertDialogResultReady(
                    AlertDialogResult(
                        messageId = message.messageID,
                        isPositive = false,
                    )
                )
            }
        }
        builder.setOnCancelListener { _: DialogInterface? ->
            logDebug("alertDialog result negative", TAG)
            alertDialogResultListener?.onAlertDialogResultReady(
                AlertDialogResult(
                    messageId = message.messageID,
                    isPositive = false,
                )
            )
        }
        messageDialog = builder.create().also { dialog ->
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(false)
            dialog.show()
        }
    }

    override fun showFullscreenLoader(message: StringSource?) {
        fullscreenLoaderView?.let { loader ->
            message?.let {
                loader.setMessage(message = it)
            }
        } ?: run {
            fullscreenLoaderView = FullscreenLoaderView(this).also { loader ->
                message?.let {
                    loader.setMessage(message = it)
                }
                loader.show()
            }
        }
    }

    override fun hideFullscreenLoader() {
        fullscreenLoaderView = try {
            fullscreenLoaderView?.dismiss()
            null
        } catch (exception: Exception) {
            logError("Hiding fullscreen loader returned an exception: ${exception.message}", TAG)
            null
        }
    }

    override fun onResume() {
        logDebug("onResume", TAG)
        super.onResume()
        viewModel.onResume()
    }

    @CallSuper
    override fun onPause() {
        logDebug("onPause", TAG)
        hideKeyboard()
        super.onPause()
        viewModel.onPause()
    }

    @CallSuper
    override fun onDestroy() {
        logDebug("onDestroy", TAG)
        super.onDestroy()
        viewModel.onDestroy()
        bannerMessageWindowManager.hideWindow()
        // Reset this activity context
        if (currentContext.get() == this) {
            currentContext.attachBaseContext(appContext)
        }
    }
}