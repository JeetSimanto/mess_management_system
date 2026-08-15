package com.messmanager.app.data.repository

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.messmanager.app.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
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

    suspend fun downloadApk(
        context: Context,
        downloadUrl: String,
        onProgress: (progress: Int, downloadedBytes: Long, totalBytes: Long) -> Unit
    ): File? = withContext(Dispatchers.IO) {
        try {
            val updatesDir = File(context.cacheDir, "updates").apply { if (!exists()) mkdirs() }
            updatesDir.listFiles()?.forEach { it.delete() }
            val apkFile = File(updatesDir, "update.apk")

            var currentUrl = downloadUrl
            var connection: HttpURLConnection
            var redirectCount = 0

            while (redirectCount < 6) {
                val url = URL(currentUrl)
                connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("User-Agent", "MessManagerApp/1.0")
                connection.connectTimeout = 15000
                connection.readTimeout = 15000
                connection.instanceFollowRedirects = true

                val status = connection.responseCode
                if (status == HttpURLConnection.HTTP_MOVED_TEMP || 
                    status == HttpURLConnection.HTTP_MOVED_PERM || 
                    status == 307 || 
                    status == 308) {
                    val newUrl = connection.getHeaderField("Location")
                    if (!newUrl.isNullOrEmpty()) {
                        currentUrl = newUrl
                        redirectCount++
                        continue
                    }
                }

                if (status != HttpURLConnection.HTTP_OK) {
                    return@withContext null
                }

                val totalBytes = connection.contentLengthLong
                val inputStream = connection.inputStream
                val outputStream = apkFile.outputStream()

                val buffer = ByteArray(8192)
                var bytesRead: Int
                var totalDownloaded: Long = 0

                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                    totalDownloaded += bytesRead
                    val progress = if (totalBytes > 0) ((totalDownloaded * 100) / totalBytes).toInt() else 0
                    onProgress(progress, totalDownloaded, totalBytes)
                }

                outputStream.flush()
                outputStream.close()
                inputStream.close()
                return@withContext apkFile
            }
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun installApk(context: Context, apkFile: File) {
        try {
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                apkFile
            )
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/vnd.android.package-archive")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
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
