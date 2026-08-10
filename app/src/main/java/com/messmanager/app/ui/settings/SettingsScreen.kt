package com.messmanager.app.ui.settings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.messmanager.app.ui.components.appTextFieldColors
import com.messmanager.app.ui.theme.DarkPrimaryGlow
import com.messmanager.app.ui.theme.DarkSurface
import com.messmanager.app.ui.theme.RadiusLg
import com.messmanager.app.util.DateUtils

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigateToWelcome: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    if (uiState.isLoading) {
        Box(
            modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    val mess = uiState.activeMess ?: return

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // Mess Info & Invite Code Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(RadiusLg))
                .background(DarkPrimaryGlow)
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = mess.name,
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    if (uiState.isManager) {
                        Text(
                            text = "MANAGER",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = DateUtils.formatMonthYear(mess.month, mess.year),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Invite Code Box
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(RadiusLg))
                        .background(DarkSurface)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "INVITE CODE",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = mess.inviteCode,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Mess Invite Code", mess.inviteCode)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Invite code copied!", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Icon(
                            Icons.Default.ContentCopy,
                            contentDescription = "Copy Invite Code",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        if (uiState.isManager) {
            Spacer(modifier = Modifier.height(16.dp))

            var fixedMealInput by remember(mess.fixedMealCount) {
                mutableStateOf(if (mess.fixedMealCount > 0.0) mess.fixedMealCount.toString() else "")
            }
            var showConfirmDialog by remember { mutableStateOf(false) }

            // Manager Controls Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(RadiusLg))
                    .background(DarkSurface)
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "MANAGER SETTINGS",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = fixedMealInput,
                            onValueChange = { fixedMealInput = it },
                            label = { Text("Fixed Minimum Meal") },
                            placeholder = { Text("e.g. 25") },
                            singleLine = true,
                            colors = appTextFieldColors(),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(RadiusLg)
                        )

                        Button(
                            onClick = {
                                val count = fixedMealInput.toDoubleOrNull() ?: 0.0
                                viewModel.updateFixedMealCount(count)
                            },
                            shape = RoundedCornerShape(RadiusLg),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Save", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.background)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (!mess.isSettled) {
                        Button(
                            onClick = { showConfirmDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(RadiusLg),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Text("Confirm Settlement", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.background)
                        }
                    } else {
                        Text(
                            text = "✓ Month Settlement Confirmed",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            if (showConfirmDialog) {
                AlertDialog(
                    onDismissRequest = { showConfirmDialog = false },
                    title = { Text("Confirm Month Settlement?", fontWeight = FontWeight.Bold) },
                    text = {
                        Text("This will finalize all meal, grocery, and contribution calculations for this month. Members below the fixed meal range will be charged for the minimum fixed meals.")
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.confirmSettlement()
                                showConfirmDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Confirm & Close Month", fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showConfirmDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Members List
        Text(
            text = "Mess Members (${uiState.members.size})",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(RadiusLg))
                .background(DarkSurface)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(uiState.members, key = { it.uid }) { member ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = member.displayName,
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (member.uid == mess.managerId) {
                                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = "Manager",
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                        Text(
                            text = member.email,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (uiState.isManager && member.uid != mess.managerId) {
                        Row {
                            IconButton(onClick = { viewModel.transferManager(member.uid) }) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = "Make Manager",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }

                            IconButton(onClick = { viewModel.removeMember(member.uid) }) {
                                Icon(
                                    Icons.Default.PersonRemove,
                                    contentDescription = "Remove Member",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action Buttons: Leave / Sign Out
        if (!uiState.isManager) {
            OutlinedButton(
                onClick = { viewModel.leaveMess() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(RadiusLg)
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                Text("Leave Mess", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        Button(
            onClick = { viewModel.signOut() },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(RadiusLg),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            )
        ) {
            Text("Sign Out", fontWeight = FontWeight.Bold)
        }
    }
}
