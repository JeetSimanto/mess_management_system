package com.messmanager.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.messmanager.app.ui.theme.DarkPrimary
import com.messmanager.app.ui.theme.DarkPrimaryGlow
import com.messmanager.app.ui.theme.DarkSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    messName: String,
    monthYearText: String,
    onOpenSettings: () -> Unit,
    onOpenBorrows: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground
        ),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Mess Avatar Circle
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(DarkPrimaryGlow),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = messName.take(1).uppercase().ifEmpty { "M" },
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = DarkPrimary
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = onOpenBorrows) {
                Icon(
                    imageVector = Icons.Default.Handshake,
                    contentDescription = "Borrow Requests",
                    tint = DarkSecondary
                )
            }

            IconButton(onClick = onOpenSettings) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    )
}
