package com.messmanager.app.ui.components

import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.messmanager.app.ui.theme.DarkOutline
import com.messmanager.app.ui.theme.DarkPrimary
import com.messmanager.app.ui.theme.DarkSurfaceHigh

@Composable
fun appTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = DarkSurfaceHigh,
    unfocusedContainerColor = DarkSurfaceHigh,
    disabledContainerColor = DarkSurfaceHigh,
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    disabledTextColor = Color.White.copy(alpha = 0.6f),
    focusedLabelColor = DarkPrimary,
    unfocusedLabelColor = Color.White.copy(alpha = 0.8f),
    focusedPlaceholderColor = Color.White.copy(alpha = 0.5f),
    unfocusedPlaceholderColor = Color.White.copy(alpha = 0.5f),
    focusedBorderColor = DarkPrimary,
    unfocusedBorderColor = DarkOutline
)
