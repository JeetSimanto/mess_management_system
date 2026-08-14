package com.messmanager.app.ui.meal

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
        // Sticky Header Row: DATE column header + Member columns (Horizontal Scroll)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(RadiusSm))
                .background(DarkSurfaceHigh.copy(alpha = 0.5f))
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // DATE Header Box
            Box(
                modifier = Modifier
                    .width(54.dp)
                    .height(44.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "DATE",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = DarkPrimary
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Member Name Column Headers (Scrollable)
            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(horizontalScrollState)
            ) {
                members.forEachIndexed { index, member ->
                    val avatarColor = AvatarColors[index % AvatarColors.size]

                    Row(
                        modifier = Modifier
                            .width(84.dp)
                            .height(44.dp)
                            .padding(horizontal = 2.dp)
                            .clip(RoundedCornerShape(RadiusSm))
                            .background(DarkSurfaceHigh)
                            .padding(horizontal = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
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

                        Spacer(modifier = Modifier.width(6.dp))

                        Text(
                            text = member.displayName,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Body Rows: One row per Date (1..daysInMonth)
        dates.forEachIndexed { dayIndex, dateIso ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Date Number Label Column
                Box(
                    modifier = Modifier
                        .width(54.dp)
                        .height(40.dp)
                        .clip(RoundedCornerShape(RadiusSm))
                        .background(DarkSurfaceHigh),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${dayIndex + 1}",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Member Meal Cells Row (Scrollable, synced with header)
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .horizontalScroll(horizontalScrollState)
                ) {
                    members.forEach { member ->
                        val mealDoc = meals.find { it.memberUid == member.uid && it.date == dateIso }
                        val currentCount = mealDoc?.count ?: 0.0

                        MealCell(
                            currentCount = currentCount,
                            isManager = isManager,
                            onClick = {
                                val nextCount = getNextMealCount(currentCount)
                                onMealClick(member.uid, member.displayName, dateIso, nextCount)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MealCell(
    currentCount: Double,
    isManager: Boolean,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 1.0f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 400f),
        label = "meal_cell_scale"
    )

    val targetBg = getMealCellBg(currentCount)
    val animatedBg by animateColorAsState(
        targetValue = targetBg,
        animationSpec = spring(stiffness = 300f),
        label = "meal_cell_bg"
    )

    val cellTextColor = if (currentCount > 0) DarkPrimary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
    val displayStr = formatMealCountDisplay(currentCount)

    Box(
        modifier = Modifier
            .padding(2.dp)
            .width(84.dp)
            .height(40.dp)
            .scale(scale)
            .clip(RoundedCornerShape(RadiusSm))
            .background(animatedBg)
            .clickable(enabled = isManager) {
                isPressed = true
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = displayStr,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = cellTextColor
        )
    }

    if (isPressed) {
        androidx.compose.runtime.LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(100)
            isPressed = false
        }
    }
}

private fun formatMealCountDisplay(count: Double): String {
    return when (count) {
        0.0 -> "-"
        0.5 -> "½"
        1.0 -> "1"
        1.5 -> "1½"
        2.0 -> "2"
        2.5 -> "2½"
        3.0 -> "3"
        else -> if (count % 1.0 == 0.0) "${count.toInt()}" else "$count"
    }
}

private fun getMealCellBg(count: Double): Color {
    return when (count) {
        0.0 -> DarkSurfaceHigh
        0.5 -> Color(0x3800E5A0)
        1.0 -> Color(0x5500E5A0)
        1.5 -> Color(0x7700E5A0)
        2.0 -> Color(0x9900E5A0)
        2.5 -> Color(0xBB00E5A0)
        3.0 -> Color(0xDD00E5A0)
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

