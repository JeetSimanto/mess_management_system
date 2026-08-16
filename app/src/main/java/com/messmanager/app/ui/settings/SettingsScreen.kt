package com.messmanager.app.ui.settings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.House
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.messmanager.app.BuildConfig
import com.messmanager.app.ui.components.UpdateDialog
import com.messmanager.app.ui.components.appTextFieldColors
import com.messmanager.app.ui.theme.AvatarColors
import com.messmanager.app.ui.theme.DarkBackground
import com.messmanager.app.ui.theme.DarkOutline
import com.messmanager.app.ui.theme.DarkPrimary
import com.messmanager.app.ui.theme.DarkPrimaryGlow
import com.messmanager.app.ui.theme.DarkSecondary
import com.messmanager.app.ui.theme.DarkSurface
import com.messmanager.app.ui.theme.DarkSurfaceHigh
import com.messmanager.app.ui.theme.PositiveDark
import com.messmanager.app.ui.theme.PositiveDarkBg
import com.messmanager.app.ui.theme.RadiusLg
import com.messmanager.app.ui.theme.RadiusSm
import com.messmanager.app.util.DateUtils
import java.time.LocalDate

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigateToWelcome: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var showCreateDialog by remember { mutableStateOf(false) }
    var showJoinDialog by remember { mutableStateOf(false) }
    var showMessesModal by remember { mutableStateOf(false) }
    var showMembersModal by remember { mutableStateOf(false) }
    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.setDailyNotificationEnabled(context, true)
        } else {
            Toast.makeText(context, "Notification permission is required for daily meal alerts.", Toast.LENGTH_LONG).show()
        }
    }

    val timePickerDialog = remember(uiState.notificationHour, uiState.notificationMinute) {
        android.app.TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                viewModel.updateNotificationTime(context, hourOfDay, minute)
            },
            uiState.notificationHour,
            uiState.notificationMinute,
            false
        )
    }

    LaunchedEffect(uiState.error, uiState.successMessage) {
        uiState.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearMessages()
        }
        uiState.successMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
        }
    }

    if (uiState.isLoading) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    val mess = uiState.activeMess
    val user = uiState.user

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Page Title Header (SETTINGS matching design)
        Text(
            text = "SETTINGS",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = androidx.compose.ui.unit.TextUnit(2f, androidx.compose.ui.unit.TextUnitType.Sp)
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Profile Header Block (Matching Reference Image Profile Card)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(DarkSurface)
                .border(BorderStroke(1.dp, DarkOutline), RoundedCornerShape(20.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val displayName = user?.displayName ?: "User"
            val initial = displayName.take(1).uppercase()

            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(DarkPrimaryGlow),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initial,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = DarkPrimary
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = displayName,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = user?.email ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (uiState.isManager) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(RadiusSm))
                        .background(DarkPrimaryGlow)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "MANAGER",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = DarkPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // SECTION 1: PREFERENCES & MESS MANAGEMENT (Grouped Card)
        SettingSectionHeader(title = "PREFERENCES & MESS")

        Spacer(modifier = Modifier.height(8.dp))

        SettingGroupCard {
            if (mess != null) {
                // Active Mess Item
                SettingRowItem(
                    icon = Icons.Default.House,
                    title = mess.name,
                    subtitle = "Code: ${mess.inviteCode} · ${DateUtils.formatMonthYear(mess.month, mess.year)}",
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Mess Invite Code", mess.inviteCode)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Invite code copied!", Toast.LENGTH_SHORT).show()
                    },
                    trailingContent = {
                        Icon(
                            Icons.Default.ContentCopy,
                            contentDescription = "Copy Code",
                            tint = DarkPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )

                SettingItemDivider()

                // My Messes Switcher Item
                SettingRowItem(
                    icon = Icons.Default.House,
                    title = "My Messes",
                    subtitle = "${uiState.userMesses.size} messes joined",
                    onClick = { showMessesModal = true }
                )

                SettingItemDivider()

                // Mess Members Item
                SettingRowItem(
                    icon = Icons.Default.People,
                    title = "Mess Members",
                    subtitle = "${uiState.members.size} members active",
                    onClick = { showMembersModal = true }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // SECTION 2: PREFERENCES & NOTIFICATIONS (Grouped Card)
        SettingSectionHeader(title = "PREFERENCES & NOTIFICATIONS")

        Spacer(modifier = Modifier.height(8.dp))

        SettingGroupCard {
            SettingRowItem(
                icon = Icons.Default.Notifications,
                title = "Daily Meal Summary",
                subtitle = if (uiState.isDailyNotificationEnabled)
                    "Daily alert scheduled at ${uiState.formattedNotificationTime}"
                else
                    "Automated daily meal summary disabled",
                trailingContent = {
                    Switch(
                        checked = uiState.isDailyNotificationEnabled,
                        onCheckedChange = { enabled ->
                            if (enabled && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                                val permissionCheck = androidx.core.content.ContextCompat.checkSelfPermission(
                                    context,
                                    android.Manifest.permission.POST_NOTIFICATIONS
                                )
                                if (permissionCheck != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                                    permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                                    return@Switch
                                }
                            }
                            viewModel.setDailyNotificationEnabled(context, enabled)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = DarkBackground,
                            checkedTrackColor = DarkPrimary,
                            uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            uncheckedTrackColor = DarkSurface
                        )
                    )
                }
            )

            if (uiState.isDailyNotificationEnabled) {
                SettingItemDivider()

                SettingRowItem(
                    icon = Icons.Default.AccessTime,
                    title = "Notification Time",
                    subtitle = "Tap to change scheduled time (${uiState.formattedNotificationTime})",
                    onClick = { timePickerDialog.show() }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // SECTION 3: APP & UPDATES (Grouped Card)
        SettingSectionHeader(title = "APP & UPDATES")

        Spacer(modifier = Modifier.height(8.dp))

        SettingGroupCard {
            SettingRowItem(
                icon = Icons.Default.SystemUpdate,
                title = "Quick Check Update",
                subtitle = "Current version: v${BuildConfig.VERSION_NAME}",
                onClick = { if (!uiState.isCheckingUpdate) viewModel.checkForUpdates() },
                trailingContent = {
                    if (uiState.isCheckingUpdate) {
                        CircularProgressIndicator(
                            color = DarkPrimary,
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                    } else if (uiState.updateInfo != null && uiState.updateInfo!!.hasUpdate) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(RadiusSm))
                                .background(PositiveDarkBg)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "v${uiState.updateInfo!!.latestVersion} AVAILABLE",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = PositiveDark
                            )
                        }
                    } else {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // SECTION 3: ACCOUNT & DANGER ZONE (Grouped Card)
        SettingSectionHeader(title = "ACCOUNT & SECURITY")

        Spacer(modifier = Modifier.height(8.dp))

        SettingGroupCard {
            if (mess != null) {
                if (uiState.isManager) {
                    SettingRowItem(
                        icon = Icons.Default.Delete,
                        title = "Delete Mess",
                        subtitle = "Permanently remove active mess",
                        iconTint = MaterialTheme.colorScheme.error,
                        titleColor = MaterialTheme.colorScheme.error,
                        onClick = { showDeleteConfirmationDialog = true }
                    )
                } else {
                    SettingRowItem(
                        icon = Icons.AutoMirrored.Filled.ExitToApp,
                        title = "Leave Active Mess",
                        subtitle = "Leave current mess group",
                        iconTint = MaterialTheme.colorScheme.error,
                        titleColor = MaterialTheme.colorScheme.error,
                        onClick = { viewModel.leaveMess() }
                    )
                }

                SettingItemDivider()
            }

            SettingRowItem(
                icon = Icons.AutoMirrored.Filled.ExitToApp,
                title = "Sign Out",
                subtitle = "Log out from your account",
                iconTint = MaterialTheme.colorScheme.error,
                titleColor = MaterialTheme.colorScheme.error,
                onClick = { viewModel.signOut() }
            )
        }
    }

    // Modal: MY MESSES (Matching Reference Modal Design)
    if (showMessesModal) {
        Dialog(onDismissRequest = { showMessesModal = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(DarkSurface)
                    .border(BorderStroke(1.dp, DarkOutline), RoundedCornerShape(24.dp)),
                color = DarkSurface
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Modal Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "MY MESSES",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = androidx.compose.ui.unit.TextUnit(1.5f, androidx.compose.ui.unit.TextUnitType.Sp)
                            ),
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        IconButton(
                            onClick = { showMessesModal = false },
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(DarkSurfaceHigh)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Messes List
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        uiState.userMesses.forEach { item ->
                            val isActive = item.id == uiState.activeMess?.id

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(RadiusLg))
                                    .background(if (isActive) DarkSurfaceHigh else DarkSurface)
                                    .clickable {
                                        if (!isActive) {
                                            viewModel.switchActiveMess(item.id)
                                            showMessesModal = false
                                        }
                                    }
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.House,
                                        contentDescription = null,
                                        tint = if (isActive) DarkPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(20.dp)
                                    )

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Text(
                                            text = item.name,
                                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "${DateUtils.formatMonthYear(item.month, item.year)} · ${item.memberIds.size} Members",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                if (isActive) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Active",
                                        tint = DarkPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Action Buttons (Create & Join)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                showMessesModal = false
                                showCreateDialog = true
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            shape = RoundedCornerShape(RadiusLg),
                            border = BorderStroke(1.dp, DarkPrimary)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = DarkPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Create Mess", color = DarkPrimary, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                showMessesModal = false
                                showJoinDialog = true
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            shape = RoundedCornerShape(RadiusLg),
                            colors = ButtonDefaults.buttonColors(containerColor = DarkPrimary)
                        ) {
                            Icon(Icons.Default.GroupAdd, contentDescription = null, tint = DarkBackground, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Join Mess", color = DarkBackground, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Modal: MESS MEMBERS
    if (showMembersModal && mess != null) {
        Dialog(onDismissRequest = { showMembersModal = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(DarkSurface)
                    .border(BorderStroke(1.dp, DarkOutline), RoundedCornerShape(24.dp)),
                color = DarkSurface
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Modal Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "MESS MEMBERS (${uiState.members.size})",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = androidx.compose.ui.unit.TextUnit(1.5f, androidx.compose.ui.unit.TextUnitType.Sp)
                            ),
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        IconButton(
                            onClick = { showMembersModal = false },
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(DarkSurfaceHigh)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Members List
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        uiState.members.forEachIndexed { index, member ->
                            val avatarColor = AvatarColors[index % AvatarColors.size]

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(RadiusLg))
                                    .background(DarkSurfaceHigh)
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(avatarColor.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = member.displayName.take(1).uppercase(),
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = avatarColor
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = member.displayName,
                                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            if (member.uid == mess.managerId) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Icon(
                                                    Icons.Default.Star,
                                                    contentDescription = "Manager",
                                                    tint = DarkSecondary,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                        Text(
                                            text = member.email,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                if (uiState.isManager && member.uid != mess.managerId) {
                                    Row {
                                        IconButton(onClick = { viewModel.transferManager(member.uid) }) {
                                            Icon(
                                                Icons.Default.Star,
                                                contentDescription = "Make Manager",
                                                tint = DarkPrimary
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
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { showMembersModal = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        shape = RoundedCornerShape(RadiusLg),
                        colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceHigh)
                    ) {
                        Text("DONE", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }
    }

    // Create Mess Dialog
    if (showCreateDialog) {
        CreateMessDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { name, month, year ->
                viewModel.createMess(name, month, year)
                showCreateDialog = false
            }
        )
    }

    // Join Mess Dialog
    if (showJoinDialog) {
        JoinMessDialog(
            onDismiss = { showJoinDialog = false },
            onJoin = { code ->
                viewModel.joinMess(code)
                showJoinDialog = false
            }
        )
    }

    // Delete Mess Confirmation Dialog
    if (showDeleteConfirmationDialog && mess != null) {
        Dialog(onDismissRequest = { showDeleteConfirmationDialog = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(DarkSurface)
                    .border(BorderStroke(1.dp, DarkOutline), RoundedCornerShape(20.dp)),
                color = DarkSurface
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Delete Mess",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = mess.name,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Are you sure you want to permanently delete '${mess.name}'? All member connections to this mess will be removed. This action cannot be undone.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showDeleteConfirmationDialog = false }) {
                            Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                showDeleteConfirmationDialog = false
                                viewModel.deleteMess()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(RadiusSm)
                        ) {
                            Text("Delete Mess", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Update Dialog Popup
    uiState.updateInfo?.let { info ->
        if (info.hasUpdate) {
            UpdateDialog(
                updateInfo = info,
                onDismiss = { viewModel.dismissUpdateDialog() }
            )
        }
    }
}

@Composable
private fun SettingSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            letterSpacing = androidx.compose.ui.unit.TextUnit(1.2f, androidx.compose.ui.unit.TextUnitType.Sp)
        ),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(horizontal = 4.dp)
    )
}

@Composable
private fun SettingGroupCard(
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(DarkSurface)
            .border(BorderStroke(1.dp, DarkOutline), RoundedCornerShape(20.dp))
            .padding(vertical = 4.dp),
        content = content
    )
}

@Composable
private fun SettingRowItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    iconTint: Color = DarkPrimary,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: (() -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = titleColor
                )
                if (!subtitle.isNullOrBlank()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        if (trailingContent != null) {
            trailingContent()
        } else {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun SettingItemDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(1.dp)
            .background(DarkOutline.copy(alpha = 0.5f))
    )
}

@Composable
private fun CreateMessDialog(
    onDismiss: () -> Unit,
    onCreate: (name: String, month: Int, year: Int) -> Unit
) {
    var name by remember { mutableStateOf("") }
    val currentMonth = remember { LocalDate.now().monthValue }
    val currentYear = remember { LocalDate.now().year }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(20.dp))
                .background(DarkSurface)
                .border(BorderStroke(1.dp, DarkOutline), RoundedCornerShape(20.dp)),
            color = DarkSurface
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Create New Mess",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Mess Name") },
                    colors = appTextFieldColors(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(RadiusSm),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                onCreate(name.trim(), currentMonth, currentYear)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkPrimary),
                        shape = RoundedCornerShape(RadiusSm)
                    ) {
                        Text("Create", color = DarkBackground, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun JoinMessDialog(
    onDismiss: () -> Unit,
    onJoin: (code: String) -> Unit
) {
    var code by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(20.dp))
                .background(DarkSurface)
                .border(BorderStroke(1.dp, DarkOutline), RoundedCornerShape(20.dp)),
            color = DarkSurface
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Join Existing Mess",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it.uppercase() },
                    label = { Text("Enter 6-Character Invite Code") },
                    colors = appTextFieldColors(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(RadiusSm),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (code.isNotBlank()) {
                                onJoin(code.trim())
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DarkPrimary),
                        shape = RoundedCornerShape(RadiusSm)
                    ) {
                        Text("Join", color = DarkBackground, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
