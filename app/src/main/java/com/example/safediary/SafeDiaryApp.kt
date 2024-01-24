package com.example.safediary

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class SafeDiaryApp: Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@SafeDiaryApp)
            modules(appModule)
        }
    }
}