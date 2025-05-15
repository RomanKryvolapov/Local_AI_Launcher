/**
 * Created & Copyright 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher

import android.app.Application
import com.romankryvolapov.localailauncher.common.models.common.LogUtil.logDebug
import com.romankryvolapov.localailauncher.data.di.dataModules
import com.romankryvolapov.localailauncher.di.appModules
import com.romankryvolapov.localailauncher.domain.di.domainModules
import com.romankryvolapov.localailauncher.domain.externalFilesDirectory
import com.romankryvolapov.localailauncher.domain.internalFilesDirectory
import leakcanary.LeakCanary
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App : Application() {

    companion object {
        private const val TAG = "AppTag"
    }

    override fun onCreate() {
        super.onCreate()
        logDebug("onCreate", TAG)
        externalFilesDirectory = getExternalFilesDir(null)!!
        internalFilesDirectory = filesDir
        setupKoin()
        configureLeakCanary()
    }

    private fun setupKoin() {
        startKoin {
            androidContext(this@App)
            if (BuildConfig.DEBUG) {
                androidLogger(Level.ERROR)
            }
            allowOverride(override = true)
            modules(appModules, domainModules, dataModules)
        }
    }

    private fun configureLeakCanary(isEnable: Boolean = false) {
        LeakCanary.config = LeakCanary.config.copy(dumpHeap = isEnable)
        LeakCanary.showLeakDisplayActivityLauncherIcon(isEnable)
    }

}