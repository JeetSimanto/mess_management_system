package com.messmanager.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.messmanager.app.util.NotificationScheduler

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED ||
            intent?.action == Intent.ACTION_MY_PACKAGE_REPLACED) {
            NotificationScheduler.updateScheduleFromPreferences(context)
        }
    }
}
