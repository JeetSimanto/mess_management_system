package com.messmanager.app.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.messmanager.app.ui.dashboard.components.CircleStatWidget
import com.messmanager.app.ui.dashboard.components.ManagerDashboardView
import com.messmanager.app.ui.dashboard.components.MoneyRemainsCard
import com.messmanager.app.ui.dashboard.components.RecentGrocerySection
import com.messmanager.app.ui.theme.DarkOutline
import com.messmanager.app.ui.theme.DarkPrimary
import com.messmanager.app.ui.theme.DarkSecondary
import com.messmanager.app.ui.theme.DarkSurface
import com.messmanager.app.util.CurrencyFormatter

@Composable
fun DashboardScreen(
    onOpenBorrows: () -> Unit,
    viewModel: DashboardViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

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

    val settlement = uiState.settlement
    if (settlement == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Text("No active mess data.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        return
    }

    AnimatedVisibility(
        visible = true,
        enter = fadeIn(animationSpec = tween(300, easing = FastOutSlowInEasing)) +
                slideInVertically(initialOffsetY = { 40 }, animationSpec = tween(300, easing = FastOutSlowInEasing))
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            if (uiState.isManager) {
                // Manager View
                ManagerDashboardView(
                    settlement = settlement,
                    onOpenBorrows = onOpenBorrows
                )
            } else {
                // Member View
                val mySettlement = settlement.memberSettlements.find { it.memberUid == uiState.currentUserId }
                val myMeals = mySettlement?.totalMeals ?: 0.0
                val myContributionPaisa = mySettlement?.totalContributionPaisa ?: 0L

                // 1. Top Circular Widgets: My Meals & My Contribution
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CircleStatWidget(
                        title = "My Meals",
                        valueText = "$myMeals",
                        subtitleText = "consumed",
                        accentColor = DarkPrimary
                    )

                    CircleStatWidget(
                        title = "My Contribution",
                        valueText = CurrencyFormatter.formatPaisa(myContributionPaisa),
                        subtitleText = "deposited",
                        accentColor = DarkSecondary
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 2. Fund Balance Card (How much money the manager currently has)
                MoneyRemainsCard(
                    moneyRemainsPaisa = settlement.moneyRemainsPaisa,
                    totalExpensePaisa = settlement.totalExpensePaisa
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 3. Borrow & Lend Requests Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(DarkSurface)
                        .border(BorderStroke(1.dp, DarkOutline), RoundedCornerShape(20.dp))
                        .clickable { onOpenBorrows() }
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Handshake,
                                    contentDescription = null,
                                    tint = DarkSecondary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Text(
                                    text = "Borrow & Lend Requests",
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Send or manage item requests with manager",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Open Borrow Requests",
                            tint = DarkSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 4. Bottom Section: Recent Grocery
                RecentGrocerySection(groceries = uiState.recentGroceries)
            }
        }
    }
}
