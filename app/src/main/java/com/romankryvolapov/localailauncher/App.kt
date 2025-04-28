/**
 * Created 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher

import android.app.Application
import com.romankryvolapov.localailauncher.BuildConfig
import leakcanary.LeakCanary
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App : Application() {

    override fun onCreate() {
        super.onCreate()
//        setupKoin()
    }

    private fun setupKoin() {
        startKoin {
            androidContext(this@App)
            if (BuildConfig.DEBUG) {
                androidLogger(Level.ERROR)
            }
            allowOverride(override = true)
//            modules(appModules, domainModules, dataModules)
        }
        configureLeakCanary()
    }

    private fun configureLeakCanary(isEnable: Boolean = false) {
        LeakCanary.config = LeakCanary.config.copy(dumpHeap = isEnable)
        LeakCanary.showLeakDisplayActivityLauncherIcon(isEnable)
    }

}