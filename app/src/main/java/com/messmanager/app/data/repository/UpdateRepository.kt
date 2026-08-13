package com.messmanager.app.data.repository

import android.content.Context
import com.messmanager.app.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

data class UpdateInfo(
    val hasUpdate: Boolean,
    val latestVersion: String,
    val releaseNotes: String,
    val downloadUrl: String
)

@Singleton
class UpdateRepository @Inject constructor() {

    suspend fun checkForUpdates(): UpdateInfo = withContext(Dispatchers.IO) {
        try {
            val url = URL("https://api.github.com/repos/JeetSimanto/mess_management_system/releases/latest")
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("Accept", "application/vnd.github.v3+json")
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            if (connection.responseCode == 200) {
                val responseText = connection.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(responseText)
                val rawTag = json.optString("tag_name", "").trim()
                val tagName = rawTag.removePrefix("v").trim()
                val releaseNotes = json.optString("body", "Bug fixes and performance improvements.")
                
                val assets = json.optJSONArray("assets")
                var downloadUrl = json.optString("html_url", "https://github.com/JeetSimanto/mess_management_system/releases")
                
                if (assets != null && assets.length() > 0) {
                    for (i in 0 until assets.length()) {
                        val asset = assets.getJSONObject(i)
                        if (asset.optString("name", "").endsWith(".apk")) {
                            downloadUrl = asset.optString("browser_download_url", downloadUrl)
                            break
                        }
                    }
                }

                val currentVersion = BuildConfig.VERSION_NAME
                val isNewer = isVersionNewer(tagName, currentVersion)

                UpdateInfo(
                    hasUpdate = isNewer,
                    latestVersion = tagName,
                    releaseNotes = releaseNotes,
                    downloadUrl = downloadUrl
                )
            } else {
                UpdateInfo(false, "", "", "")
            }
        } catch (e: Exception) {
            UpdateInfo(false, "", "", "")
        }
    }

    fun isVersionDismissed(context: Context, version: String): Boolean {
        if (version.isBlank()) return false
        val prefs = context.getSharedPreferences("app_updates_pref", Context.MODE_PRIVATE)
        val dismissedVersion = prefs.getString("dismissed_version", null)
        return dismissedVersion == version
    }

    fun dismissVersion(context: Context, version: String) {
        if (version.isBlank()) return
        val prefs = context.getSharedPreferences("app_updates_pref", Context.MODE_PRIVATE)
        prefs.edit().putString("dismissed_version", version).apply()
    }

    private fun isVersionNewer(latest: String, current: String): Boolean {
        if (latest.isBlank()) return false
        
        val cleanLatest = latest.trim().removePrefix("v").split("-")[0]
        val cleanCurrent = current.trim().removePrefix("v").split("-")[0]

        val latestParts = cleanLatest.split(".").mapNotNull { it.toIntOrNull() }
        val currentParts = cleanCurrent.split(".").mapNotNull { it.toIntOrNull() }

        if (latestParts.isEmpty() || currentParts.isEmpty()) return false

        for (i in 0 until maxOf(latestParts.size, currentParts.size)) {
            val l = latestParts.getOrElse(i) { 0 }
            val c = currentParts.getOrElse(i) { 0 }
            if (l > c) return true
            if (l < c) return false
        }
        return false
    }
}
