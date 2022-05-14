package com.example.note.base

import android.content.Context
import com.zeugmasolutions.localehelper.LocaleAwareApplication
import androidx.appcompat.app.AppCompatDelegate


class MyApplication : LocaleAwareApplication() {
    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext

    }

    override fun onLowMemory() {
        super.onLowMemory()

    }

    companion object {
       lateinit var appContext: Context
            private set

        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }
}