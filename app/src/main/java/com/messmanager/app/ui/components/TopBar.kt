package com.messmanager.app.ui.components

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.messmanager.app.ui.theme.DarkSecondary
import com.messmanager.app.ui.theme.MilkerFontFamily

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
            Text(
                text = messName.ifEmpty { "MESS MANAGER" }.uppercase(),
                fontFamily = MilkerFontFamily,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 22.sp,
                    letterSpacing = 1.sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
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
