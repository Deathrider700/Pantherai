package com.pantherai.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PantherAIApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize any app-wide configurations here
    }
} 