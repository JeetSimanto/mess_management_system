package com.messmanager.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.messmanager.app.ui.navigation.Screen
import com.messmanager.app.ui.theme.DarkPrimaryGlow
import com.messmanager.app.ui.theme.DarkSurface
import com.messmanager.app.ui.theme.RadiusLg

@Composable
fun BottomNavBar(
    currentRoute: String,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val navItems = listOf(
        NavItem(Screen.Dashboard, "Home", Icons.Default.Home),
        NavItem(Screen.Grocery, "Grocery", Icons.Default.ShoppingCart),
        NavItem(Screen.Utility, "Utility", Icons.AutoMirrored.Filled.ReceiptLong),
        NavItem(Screen.Meals, "Meals", Icons.Default.Restaurant),
        NavItem(Screen.Contributions, "Deposits", Icons.Default.AccountBalanceWallet)
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(DarkSurface)
            .padding(horizontal = 4.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        navItems.forEach { item ->
            val isSelected = currentRoute == item.screen.route

            val containerBg by animateColorAsState(
                targetValue = if (isSelected) DarkPrimaryGlow else DarkSurface,
                animationSpec = tween(200),
                label = "nav_tab_bg"
            )

            val contentColor by animateColorAsState(
                targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                animationSpec = tween(200),
                label = "nav_tab_content_color"
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(RadiusLg))
                    .background(containerBg)
                    .clickable { onNavigate(item.screen) }
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = contentColor,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = contentColor
                        )
                    )
                }
            }
        }
    }
}

private data class NavItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector
)

