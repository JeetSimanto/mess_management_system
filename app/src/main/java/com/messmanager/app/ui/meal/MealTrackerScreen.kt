package com.messmanager.app.ui.meal

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
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
import com.messmanager.app.ui.theme.CurrencyHeroStyle
import com.messmanager.app.ui.theme.DarkPrimary
import com.messmanager.app.ui.theme.DarkPrimaryGlow

@Composable
fun MealTrackerScreen(
    viewModel: MealViewModel = hiltViewModel(),
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

    val activeMess = uiState.activeMess ?: return
    val currentMember = uiState.members.find { it.uid == uiState.currentUserId }
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
                    text = if (uiState.isManager) "Tap any cell to log or update member meals" else "Your monthly meal activity calendar",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Manager View: All Members Grid Matrix
        // Member View: Member Calendar View
        if (uiState.isManager) {
            MealCalendarGrid(
                members = uiState.members,
                meals = uiState.meals,
                month = uiState.displayedMonth,
                year = uiState.displayedYear,
                isManager = true,
                onMealClick = { memberUid, memberName, dateIso, nextCount ->
                    viewModel.setMealCount(memberUid, memberName, dateIso, nextCount)
                }
            )
        } else if (currentMember != null) {
            MemberMealCalendarView(
                member = currentMember,
                meals = uiState.meals,
                month = uiState.displayedMonth,
                year = uiState.displayedYear,
                isManager = false,
                onMealClick = { memberUid, memberName, dateIso, nextCount ->
                    viewModel.setMealCount(memberUid, memberName, dateIso, nextCount)
                },
                onMonthChange = { delta ->
                    viewModel.changeMonth(delta)
                }
            )
        }
    }
}
