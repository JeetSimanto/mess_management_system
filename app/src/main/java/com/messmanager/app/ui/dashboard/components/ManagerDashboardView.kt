package com.messmanager.app.ui.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.messmanager.app.domain.model.MemberSettlement
import com.messmanager.app.domain.model.MessSettlement
import com.messmanager.app.domain.model.SettlementStatus
import com.messmanager.app.ui.theme.CurrencyHeroStyle
import com.messmanager.app.ui.theme.DarkPrimaryGlow
import com.messmanager.app.ui.theme.DarkSurface
import com.messmanager.app.ui.theme.DarkSurfaceHigh
import com.messmanager.app.ui.theme.NegativeDark
import com.messmanager.app.ui.theme.NegativeDarkBg
import com.messmanager.app.ui.theme.PositiveDark
import com.messmanager.app.ui.theme.PositiveDarkBg
import com.messmanager.app.ui.theme.RadiusLg
import com.messmanager.app.ui.theme.RadiusSm
import com.messmanager.app.util.CurrencyFormatter

@Composable
fun ManagerDashboardView(
    settlement: MessSettlement,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Hero Total Expense Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(RadiusLg))
                .background(DarkPrimaryGlow)
                .padding(20.dp)
        ) {
            Column {
                Text(
                    text = "TOTAL MESS EXPENSE",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = CurrencyFormatter.formatPaisa(settlement.totalExpensePaisa),
                    style = CurrencyHeroStyle,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Grocery: ${CurrencyFormatter.formatPaisa(settlement.totalGroceryPaisa)} · Utility: ${CurrencyFormatter.formatPaisa(settlement.totalUtilityPaisa)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4 Key Stat Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Meal Rate",
                value = CurrencyFormatter.formatMealRate(settlement.mealRateBdtPerMeal),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Total Meals",
                value = "${settlement.totalMeals}",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                title = "Fund Balance",
                value = CurrencyFormatter.formatPaisa(settlement.moneyRemainsPaisa),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Members",
                value = "${settlement.memberSettlements.size}",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Member Settlements Table
        Text(
            text = "Member Settlement Status",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(RadiusLg))
                .background(DarkSurface)
                .padding(16.dp)
        ) {
            settlement.memberSettlements.forEachIndexed { index, memberSettlement ->
                MemberSettlementRow(memberSettlement)
                if (index < settlement.memberSettlements.size - 1) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
            }
        }
    }
}

@Composable
private fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(RadiusLg))
            .background(DarkSurfaceHigh)
            .padding(14.dp)
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
private fun MemberSettlementRow(ms: MemberSettlement) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = ms.memberName,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            val mealDetailsText = if (ms.isFixedMealApplied) {
                "${ms.totalMeals} meals (Min ${ms.totalMeals}, Ate ${ms.rawMeals}) · Cost: ${CurrencyFormatter.formatPaisa(ms.totalCostPaisa)} · Paid: ${CurrencyFormatter.formatPaisa(ms.totalContributionPaisa)}"
            } else {
                "${ms.totalMeals} meals · Cost: ${CurrencyFormatter.formatPaisa(ms.totalCostPaisa)} · Paid: ${CurrencyFormatter.formatPaisa(ms.totalContributionPaisa)}"
            }
            Text(
                text = mealDetailsText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        val (badgeText, badgeBg, badgeTextColor) = when (ms.status) {
            SettlementStatus.GET_BACK -> Triple("GET ${CurrencyFormatter.formatPaisa(ms.balancePaisa)}", PositiveDarkBg, PositiveDark)
            SettlementStatus.PAY_EXTRA -> Triple("PAY ${CurrencyFormatter.formatPaisa(-ms.balancePaisa)}", NegativeDarkBg, NegativeDark)
            SettlementStatus.SETTLED -> Triple("SETTLED", DarkSurfaceHigh, MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(RadiusSm))
                .background(badgeBg)
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Text(
                text = badgeText,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = badgeTextColor
            )
        }
    }
}
