package com.messmanager.app

import android.app.Application
import com.messmanager.app.util.NotificationHelper
import com.messmanager.app.util.NotificationScheduler
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MessApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            NotificationHelper.createNotificationChannels(this)
            NotificationScheduler.updateScheduleFromPreferences(this)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
}
