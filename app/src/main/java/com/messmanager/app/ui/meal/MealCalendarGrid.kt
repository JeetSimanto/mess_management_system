package com.messmanager.app.ui.meal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.messmanager.app.domain.model.Meal
import com.messmanager.app.domain.model.Member
import com.messmanager.app.ui.theme.AvatarColors
import com.messmanager.app.ui.theme.DarkPrimary
import com.messmanager.app.ui.theme.DarkPrimaryGlow
import com.messmanager.app.ui.theme.DarkSurface
import com.messmanager.app.ui.theme.DarkSurfaceHigh
import com.messmanager.app.ui.theme.RadiusLg
import com.messmanager.app.ui.theme.RadiusSm
import java.time.YearMonth

@Composable
fun MealCalendarGrid(
    members: List<Member>,
    meals: List<Meal>,
    month: Int,
    year: Int,
    isManager: Boolean,
    onMealClick: (memberUid: String, memberName: String, dateIso: String, nextCount: Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val daysInMonth = try {
        YearMonth.of(year, month).lengthOfMonth()
    } catch (e: Exception) {
        30
    }

    val dates = (1..daysInMonth).map { day ->
        val monthStr = if (month < 10) "0$month" else "$month"
        val dayStr = if (day < 10) "0$day" else "$day"
        "$year-$monthStr-$dayStr"
    }

    val horizontalScrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(RadiusLg))
            .background(DarkSurface)
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Member Name Header
            Box(
                modifier = Modifier
                    .width(110.dp)
                    .height(44.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "MEMBER",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Dates Header Row (Scrollable)
            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(horizontalScrollState)
            ) {
                dates.forEachIndexed { index, _ ->
                    Box(
                        modifier = Modifier
                            .width(44.dp)
                            .height(44.dp)
                            .padding(2.dp)
                            .clip(RoundedCornerShape(RadiusSm))
                            .background(DarkSurfaceHigh),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${index + 1}",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = DarkPrimary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        members.forEachIndexed { index, member ->
            val avatarColor = AvatarColors[index % AvatarColors.size]

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Member Name Column with Initials Avatar
                Row(
                    modifier = Modifier
                        .width(110.dp)
                        .height(44.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(avatarColor.copy(alpha = 0.2f)),
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
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                }

                // Member Meal Cells Row (Scrollable)
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .horizontalScroll(horizontalScrollState)
                ) {
                    dates.forEach { dateIso ->
                        val mealDoc = meals.find { it.memberUid == member.uid && it.date == dateIso }
                        val currentCount = mealDoc?.count ?: 0.0

                        val cellBg = getMealCellBg(currentCount)
                        val cellTextColor = if (currentCount > 0) DarkPrimary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)

                        Box(
                            modifier = Modifier
                                .padding(2.dp)
                                .width(44.dp)
                                .height(44.dp)
                                .clip(RoundedCornerShape(RadiusSm))
                                .background(cellBg)
                                .clickable(enabled = isManager) {
                                    val nextCount = getNextMealCount(currentCount)
                                    onMealClick(member.uid, member.displayName, dateIso, nextCount)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (currentCount > 0) {
                                    if (currentCount % 1.0 == 0.0) "${currentCount.toInt()}" else "$currentCount"
                                } else "-",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = cellTextColor
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun getMealCellBg(count: Double): Color {
    return when (count) {
        0.0 -> DarkSurfaceHigh
        0.5 -> Color(0x2200E5A0)
        1.0 -> DarkPrimaryGlow
        1.5 -> Color(0x4400E5A0)
        2.0 -> Color(0x5500E5A0)
        2.5 -> Color(0x6600E5A0)
        3.0 -> Color(0x7700E5A0)
        else -> DarkSurfaceHigh
    }
}

private fun getNextMealCount(current: Double): Double {
    return when (current) {
        0.0 -> 1.0
        1.0 -> 2.0
        2.0 -> 3.0
        3.0 -> 0.5
        0.5 -> 1.5
        1.5 -> 2.5
        else -> 0.0
    }
}
