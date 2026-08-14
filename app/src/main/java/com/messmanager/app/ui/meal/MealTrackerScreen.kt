package com.messmanager.app.ui.meal

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.messmanager.app.domain.model.Member
import com.messmanager.app.ui.theme.AvatarColors
import com.messmanager.app.ui.theme.CurrencyHeroStyle
import com.messmanager.app.ui.theme.DarkOutline
import com.messmanager.app.ui.theme.DarkPrimary
import com.messmanager.app.ui.theme.DarkPrimaryGlow
import com.messmanager.app.ui.theme.DarkSurface
import com.messmanager.app.ui.theme.DarkSurfaceHigh
import com.messmanager.app.ui.theme.RadiusLg
import com.messmanager.app.ui.theme.RadiusSm

@Composable
fun MealTrackerScreen(
    viewModel: MealViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedMemberUid by remember(uiState.currentUserId, uiState.members) {
        mutableStateOf(uiState.currentUserId)
    }
    var isCalendarViewMode by remember { mutableStateOf(true) }

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

    val activeMess = uiState.activeMess ?: return
    val currentMember = uiState.members.find { it.uid == selectedMemberUid }
        ?: uiState.members.find { it.uid == uiState.currentUserId }
        ?: uiState.members.firstOrNull()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Total Meals Header Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(DarkPrimaryGlow.copy(alpha = 0.35f))
                .border(BorderStroke(1.dp, DarkPrimary.copy(alpha = 0.35f)), RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "TOTAL MEALS RECORDED",
                    style = MaterialTheme.typography.labelSmall,
                    color = DarkPrimary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${uiState.totalMeals}",
                    style = CurrencyHeroStyle,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = if (uiState.isManager) "Tap any cell or use calendar view to log meals" else "Your monthly meal activity calendar",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Manager View Selector Pill (Calendar View vs Grid View)
        if (uiState.isManager) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(RadiusLg))
                    .background(DarkSurface)
                    .border(BorderStroke(1.dp, DarkOutline), RoundedCornerShape(RadiusLg))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isCalendarViewMode) DarkPrimaryGlow else DarkSurface)
                        .clickable { isCalendarViewMode = true },
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Calendar View",
                            tint = if (isCalendarViewMode) DarkPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Calendar View",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (isCalendarViewMode) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = if (isCalendarViewMode) DarkPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (!isCalendarViewMode) DarkPrimaryGlow else DarkSurface)
                        .clickable { isCalendarViewMode = false },
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.GridView,
                            contentDescription = "Spreadsheet Grid",
                            tint = if (!isCalendarViewMode) DarkPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "All Members Grid",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (!isCalendarViewMode) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = if (!isCalendarViewMode) DarkPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Member Avatar Chip Bar (when in Calendar View for Managers)
            if (isCalendarViewMode && uiState.members.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    uiState.members.forEachIndexed { index, member ->
                        val isSelected = member.uid == selectedMemberUid
                        val avatarColor = AvatarColors[index % AvatarColors.size]

                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(RadiusLg))
                                .background(if (isSelected) DarkPrimaryGlow else DarkSurfaceHigh)
                                .border(
                                    BorderStroke(
                                        1.dp,
                                        if (isSelected) DarkPrimary else DarkOutline.copy(alpha = 0.5f)
                                    ),
                                    RoundedCornerShape(RadiusLg)
                                )
                                .clickable { selectedMemberUid = member.uid }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(avatarColor.copy(alpha = 0.25f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = member.displayName.take(1).uppercase(),
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = avatarColor
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = member.displayName,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                ),
                                color = if (isSelected) DarkPrimary else MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }
        }

        // Render Calendar View or Grid View
        if (isCalendarViewMode && currentMember != null) {
            MemberMealCalendarView(
                member = currentMember,
                meals = uiState.meals,
                month = uiState.displayedMonth,
                year = uiState.displayedYear,
                isManager = uiState.isManager,
                onMealClick = { memberUid, memberName, dateIso, nextCount ->
                    viewModel.setMealCount(memberUid, memberName, dateIso, nextCount)
                },
                onMonthChange = { delta ->
                    viewModel.changeMonth(delta)
                }
            )
        } else {
            MealCalendarGrid(
                members = uiState.members,
                meals = uiState.meals,
                month = uiState.displayedMonth,
                year = uiState.displayedYear,
                isManager = uiState.isManager,
                onMealClick = { memberUid, memberName, dateIso, nextCount ->
                    viewModel.setMealCount(memberUid, memberName, dateIso, nextCount)
                }
            )
        }
    }
}
