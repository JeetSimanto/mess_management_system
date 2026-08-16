package com.messmanager.app.data.local

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DailyNotificationPreferences @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    var isEnabled: Boolean
        get() = prefs.getBoolean(KEY_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_ENABLED, value).apply()

    var hour: Int
        get() = prefs.getInt(KEY_HOUR, DEFAULT_HOUR)
        set(value) = prefs.edit().putInt(KEY_HOUR, value).apply()

    var minute: Int
        get() = prefs.getInt(KEY_MINUTE, DEFAULT_MINUTE)
        set(value) = prefs.edit().putInt(KEY_MINUTE, value).apply()

    fun getFormattedTime(): String {
        val h = hour
        val m = minute
        val amPm = if (h >= 12) "PM" else "AM"
        val displayHour = when {
            h == 0 -> 12
            h > 12 -> h - 12
            else -> h
        }
        return String.format(java.util.Locale.US, "%02d:%02d %s", displayHour, m, amPm)
    }

    companion object {
        private const val PREF_NAME = "daily_notification_pref"
        private const val KEY_ENABLED = "daily_notification_enabled"
        private const val KEY_HOUR = "daily_notification_hour"
        private const val KEY_MINUTE = "daily_notification_minute"

        const val DEFAULT_HOUR = 21 // 9:00 PM
        const val DEFAULT_MINUTE = 0
    }
}
