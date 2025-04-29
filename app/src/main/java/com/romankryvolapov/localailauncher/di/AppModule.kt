/**
 * single<Class>(named("name")){Class()} -> for creating a specific instance in module
 * single<Class1>{Class1(get<Class2>(named("name")))} -> for creating a specific instance in module
 * val nameOfVariable: Class by inject(named("name")) -> for creating a specific instance in class
 * get<Class>{parametersOf("param")} -> parameter passing in module
 * single<Class>{(param: String)->Class(param)} -> parameter passing in module
 * val name: Class by inject(parameters={parametersOf("param")}) -> parameter passing in class
 * Created 2025 by Roman Kryvolapov
 */
package com.romankryvolapov.localailauncher.di

import android.app.DownloadManager
import android.app.NotificationManager
import android.content.ClipboardManager
import android.os.Handler
import android.os.Looper
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import com.romankryvolapov.localailauncher.data.utils.CoroutineContextProvider
import com.romankryvolapov.localailauncher.utils.CurrentContext
import com.romankryvolapov.localailauncher.utils.InactivityTimer
import com.romankryvolapov.localailauncher.utils.InactivityTimerImpl
import com.romankryvolapov.localailauncher.utils.LocalizationManager
import com.romankryvolapov.localailauncher.utils.LoginTimer
import com.romankryvolapov.localailauncher.utils.LoginTimerImpl
import com.romankryvolapov.localailauncher.utils.NotificationHelper
import com.romankryvolapov.localailauncher.utils.RecyclerViewAdapterDataObserver
import com.romankryvolapov.localailauncher.utils.SocialNetworksHelper
import com.romankryvolapov.localailauncher.utils.SupportBiometricManager
import com.romankryvolapov.localailauncher.utils.SupportBiometricManagerImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {

    single<ClipboardManager> {
        ContextCompat.getSystemService(
            androidContext(),
            ClipboardManager::class.java
        ) as ClipboardManager
    }

    single<DownloadManager> {
        ContextCompat.getSystemService(
            androidContext(),
            DownloadManager::class.java
        ) as DownloadManager
    }

    single<InputMethodManager> {
        ContextCompat.getSystemService(
            androidContext(),
            InputMethodManager::class.java
        ) as InputMethodManager
    }

    single<NotificationManager> {
        ContextCompat.getSystemService(
            androidContext(),
            NotificationManager::class.java
        ) as NotificationManager
    }

    single<CoroutineContextProvider> {
        CoroutineContextProvider()
    }

    single<NotificationHelper> {
        NotificationHelper()
    }

    single<Handler> {
        Handler(Looper.getMainLooper())
    }

    single<SocialNetworksHelper> {
        SocialNetworksHelper()
    }

    single<SupportBiometricManager> {
        SupportBiometricManagerImpl()
    }

    single<RecyclerViewAdapterDataObserver> {
        RecyclerViewAdapterDataObserver()
    }

    single<CurrentContext> {
        CurrentContext(
            context = androidContext()
        )
    }

    single<LocalizationManager> {
        LocalizationManager()
    }

    single<LoginTimer> {
        LoginTimerImpl()
    }

    single<InactivityTimer> {
        InactivityTimerImpl()
    }

}