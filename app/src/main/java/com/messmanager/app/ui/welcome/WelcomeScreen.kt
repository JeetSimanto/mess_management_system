package com.messmanager.app.ui.welcome

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddHome
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.messmanager.app.ui.theme.DarkPrimary
import com.messmanager.app.ui.theme.DarkPrimaryGlow
import com.messmanager.app.ui.theme.DarkSurfaceHigh
import com.messmanager.app.ui.theme.RadiusLg
import com.messmanager.app.ui.theme.RadiusSm

@Composable
fun WelcomeScreen(
    uiState: AuthUiState,
    onGoogleSignInClick: () -> Unit,
    onCreateMessClick: () -> Unit,
    onJoinMessClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // App Logo Hero Icon
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Mess Manager Logo",
                    tint = DarkPrimary,
                    modifier = Modifier.size(52.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Mess Manager",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "The smartest way to manage shared house expenses, meals & settlements.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Feature Pills Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                FeatureChip("Meal Tracker")
                Spacer(modifier = Modifier.width(8.dp))
                FeatureChip("Equal Split")
                Spacer(modifier = Modifier.width(8.dp))
                FeatureChip("Borrow Requests")
            }

            Spacer(modifier = Modifier.height(48.dp))

            if (uiState.isLoading) {
                CircularProgressIndicator(color = DarkPrimary)
            } else if (uiState.user == null) {
                // Google Sign In Button
                Button(
                    onClick = onGoogleSignInClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(RadiusLg),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkPrimary,
                        contentColor = MaterialTheme.colorScheme.background
                    )
                ) {
                    Text(
                        text = "Sign in with Google",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                // User is signed in -> show Create / Join options
                Text(
                    text = "Welcome back, ${uiState.user.displayName}!",
                    style = MaterialTheme.typography.titleLarge,
                    color = DarkPrimary,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onCreateMessClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(RadiusLg),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkPrimary,
                        contentColor = MaterialTheme.colorScheme.background
                    )
                ) {
                    Icon(imageVector = Icons.Default.AddHome, contentDescription = null)
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = "Create a New Mess",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = onJoinMessClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(RadiusLg)
                ) {
                    Icon(
                        imageVector = Icons.Default.GroupAdd,
                        contentDescription = null,
                        tint = DarkPrimary
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = "Join Mess with Invite Code",
                        style = MaterialTheme.typography.labelLarge,
                        color = DarkPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            uiState.error?.let { error ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun FeatureChip(label: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(RadiusSm))
            .background(DarkSurfaceHigh)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
