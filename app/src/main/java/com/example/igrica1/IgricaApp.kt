package com.example.igrica1

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class IgricaApp : Application() {

    override fun onCreate() {
        super.onCreate()
        try {
            startKoin {
                androidContext(this@IgricaApp)
                modules(koinModule)

            }
        } catch (e: Exception) {
        }
    }
}