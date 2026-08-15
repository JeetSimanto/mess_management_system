package com.messmanager.app.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.messmanager.app.data.repository.UpdateInfo
import com.messmanager.app.data.repository.UpdateRepository
import com.messmanager.app.ui.theme.DarkBackground
import com.messmanager.app.ui.theme.DarkOutline
import com.messmanager.app.ui.theme.DarkPrimary
import com.messmanager.app.ui.theme.DarkPrimaryGlow
import com.messmanager.app.ui.theme.DarkSurface
import com.messmanager.app.ui.theme.DarkSurfaceHigh
import com.messmanager.app.ui.theme.RadiusLg
import com.messmanager.app.ui.theme.RadiusMd
import com.messmanager.app.ui.theme.RadiusSm
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale

@Composable
fun UpdateDialog(
    updateInfo: UpdateInfo,
    updateRepository: UpdateRepository = remember { UpdateRepository() },
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isDownloading by remember { mutableStateOf(false) }
    var downloadProgress by remember { mutableIntStateOf(0) }
    var downloadedBytes by remember { mutableLongStateOf(0L) }
    var totalBytes by remember { mutableLongStateOf(0L) }
    var downloadError by remember { mutableStateOf<String?>(null) }
    var downloadedFile by remember { mutableStateOf<File?>(null) }

    Dialog(onDismissRequest = { if (!isDownloading) onDismiss() }) {
        Surface(
            shape = RoundedCornerShape(RadiusLg),
            color = DarkSurfaceHigh,
            border = BorderStroke(1.dp, DarkOutline),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.Start
            ) {
                // Header Icon & Title Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(DarkPrimaryGlow),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isDownloading) Icons.Default.Download else Icons.Default.SystemUpdate,
                            contentDescription = null,
                            tint = DarkPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column {
                        Text(
                            text = if (isDownloading) "Downloading Update" else "Update Available",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Version ${updateInfo.latestVersion}",
                            style = MaterialTheme.typography.labelMedium,
                            color = DarkPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (!isDownloading && downloadError == null) {
                    // Release Notes Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(RadiusMd))
                            .background(DarkSurface)
                            .border(BorderStroke(1.dp, DarkOutline.copy(alpha = 0.5f)), RoundedCornerShape(RadiusMd))
                            .padding(14.dp)
                    ) {
                        Column {
                            Text(
                                text = "WHAT'S NEW",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = DarkPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = updateInfo.releaseNotes.ifBlank { "Performance improvements and general bug fixes." },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Buttons Row: Later / Update Now
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text("Later", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                isDownloading = true
                                downloadError = null
                                scope.launch {
                                    val file = updateRepository.downloadApk(
                                        context = context,
                                        downloadUrl = updateInfo.downloadUrl,
                                        onProgress = { progress, downloaded, total ->
                                            downloadProgress = progress
                                            downloadedBytes = downloaded
                                            totalBytes = total
                                        }
                                    )
                                    if (file != null && file.exists()) {
                                        downloadedFile = file
                                        updateRepository.installApk(context, file)
                                        onDismiss()
                                    } else {
                                        isDownloading = false
                                        downloadError = "Failed to download update. Please try again."
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DarkPrimary,
                                contentColor = DarkBackground
                            ),
                            shape = RoundedCornerShape(RadiusSm)
                        ) {
                            Text("Update Now", fontWeight = FontWeight.Bold)
                        }
                    }
                } else if (isDownloading) {
                    // Download Progress View
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (downloadProgress >= 100) "Opening Installer..." else "Downloading APK...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "$downloadProgress%",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = DarkPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LinearProgressIndicator(
                            progress = { (downloadProgress / 100f).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = DarkPrimary,
                            trackColor = DarkSurface
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = formatFileSize(downloadedBytes),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = if (totalBytes > 0) formatFileSize(totalBytes) else "--",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else if (downloadError != null) {
                    // Error Fallback State
                    Text(
                        text = downloadError!!,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(updateInfo.downloadUrl))
                                context.startActivity(intent)
                                onDismiss()
                            }
                        ) {
                            Text("Open in Browser")
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                isDownloading = true
                                downloadError = null
                                scope.launch {
                                    val file = updateRepository.downloadApk(
                                        context = context,
                                        downloadUrl = updateInfo.downloadUrl,
                                        onProgress = { progress, downloaded, total ->
                                            downloadProgress = progress
                                            downloadedBytes = downloaded
                                            totalBytes = total
                                        }
                                    )
                                    if (file != null && file.exists()) {
                                        downloadedFile = file
                                        updateRepository.installApk(context, file)
                                        onDismiss()
                                    } else {
                                        isDownloading = false
                                        downloadError = "Failed to download update. Please try again."
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DarkPrimary,
                                contentColor = DarkBackground
                            )
                        ) {
                            Text("Retry In-App")
                        }
                    }
                }
            }
        }
    }
}

private fun formatFileSize(bytes: Long): String {
    if (bytes <= 0) return "0 MB"
    val mb = bytes.toDouble() / (1024 * 1024)
    return String.format(Locale.US, "%.1f MB", mb)
}
