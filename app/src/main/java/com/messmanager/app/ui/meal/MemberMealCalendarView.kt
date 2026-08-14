package com.messmanager.app.ui.meal

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.messmanager.app.domain.model.Meal
import com.messmanager.app.domain.model.Member
import com.messmanager.app.ui.theme.DarkBackground
import com.messmanager.app.ui.theme.DarkOutline
import com.messmanager.app.ui.theme.DarkPrimary
import com.messmanager.app.ui.theme.DarkPrimaryGlow
import com.messmanager.app.ui.theme.DarkSecondary
import com.messmanager.app.ui.theme.DarkSurface
import com.messmanager.app.ui.theme.DarkSurfaceHigh
import com.messmanager.app.ui.theme.DarkTertiary
import com.messmanager.app.ui.theme.RadiusLg
import com.messmanager.app.ui.theme.RadiusSm
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun MemberMealCalendarView(
    member: Member,
    meals: List<Meal>,
    month: Int,
    year: Int,
    isManager: Boolean,
    onMealClick: (memberUid: String, memberName: String, dateIso: String, nextCount: Double) -> Unit,
    onMonthChange: (delta: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedDay by remember(month, year) { mutableStateOf(LocalDate.now().dayOfMonth.coerceAtMost(28)) }

    val yearMonth = try {
        YearMonth.of(year, month)
    } catch (e: Exception) {
        YearMonth.now()
    }

    val daysInMonth = yearMonth.lengthOfMonth()
    val firstDayOfMonth = yearMonth.atDay(1)
    val firstDayOfWeekVal = firstDayOfMonth.dayOfWeek.value // 1 = Mon, 7 = Sun

    // Previous month padding days
    val prevMonthDaysCount = firstDayOfWeekVal - 1
    val prevMonthLength = yearMonth.minusMonths(1).lengthOfMonth()
    val prevMonthDays = ((prevMonthLength - prevMonthDaysCount + 1)..prevMonthLength).toList()

    // Next month padding days to complete grid (42 cells)
    val totalCells = 42
    val nextMonthDaysCount = totalCells - (prevMonthDaysCount + daysInMonth)
    val nextMonthDays = (1..nextMonthDaysCount).toList()

    val monthName = yearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(DarkSurface)
            .border(BorderStroke(1.dp, DarkOutline), RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        // Month & Year Header with Clickable Previous/Next Month Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onMonthChange(-1) }) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "Previous Month",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = monthName,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "$year",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = { onMonthChange(1) }) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Next Month",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Days of Week Header Row (Mon..Sun)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach { dayLabel ->
                Text(
                    text = dayLabel,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Calendar Grid (7 columns x 6 rows)
        val allGridDays = buildList {
            prevMonthDays.forEach { day -> add(CalendarCellData(day = day, isCurrentMonth = false)) }
            (1..daysInMonth).forEach { day -> add(CalendarCellData(day = day, isCurrentMonth = true)) }
            nextMonthDays.forEach { day -> add(CalendarCellData(day = day, isCurrentMonth = false)) }
        }

        val rows = allGridDays.chunked(7)
        rows.forEach { rowDays ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                rowDays.forEach { cell ->
                    val isSelected = cell.isCurrentMonth && cell.day == selectedDay

                    val dateIso = if (cell.isCurrentMonth) {
                        val monthStr = if (month < 10) "0$month" else "$month"
                        val dayStr = if (cell.day < 10) "0${cell.day}" else "${cell.day}"
                        "$year-$monthStr-$dayStr"
                    } else ""

                    val dayMealCount = if (cell.isCurrentMonth) {
                        meals.find { it.memberUid == member.uid && it.date == dateIso }?.count ?: 0.0
                    } else 0.0

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(enabled = cell.isCurrentMonth) {
                                selectedDay = cell.day
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            // Date Number Pill
                            val bgCircleColor = if (isSelected) DarkPrimary else Color.Transparent
                            val textColor = when {
                                isSelected -> DarkBackground
                                !cell.isCurrentMonth -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                else -> MaterialTheme.colorScheme.onSurface
                            }

                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(bgCircleColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${cell.day}",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    ),
                                    color = textColor
                                )
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            // Indicator Dots under date
                            if (cell.isCurrentMonth && dayMealCount > 0) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (dayMealCount >= 0.5) {
                                        Box(
                                            modifier = Modifier
                                                .size(4.dp)
                                                .clip(CircleShape)
                                                .background(if (isSelected) DarkBackground else DarkPrimary)
                                        )
                                    }
                                    if (dayMealCount >= 1.5) {
                                        Box(
                                            modifier = Modifier
                                                .size(4.dp)
                                                .clip(CircleShape)
                                                .background(if (isSelected) DarkBackground else DarkSecondary)
                                        )
                                    }
                                    if (dayMealCount >= 2.5) {
                                        Box(
                                            modifier = Modifier
                                                .size(4.dp)
                                                .clip(CircleShape)
                                                .background(if (isSelected) DarkBackground else DarkTertiary)
                                        )
                                    }
                                }
                            } else {
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Selected Day Details & Interactive Meal Logging Controls
        val monthStr = if (month < 10) "0$month" else "$month"
        val dayStr = if (selectedDay < 10) "0$selectedDay" else "$selectedDay"
        val selectedDateIso = "$year-$monthStr-$dayStr"
        val currentMealDoc = meals.find { it.memberUid == member.uid && it.date == selectedDateIso }
        val countForSelectedDay = currentMealDoc?.count ?: 0.0

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(RadiusLg))
                .background(DarkSurfaceHigh)
                .padding(14.dp)
        ) {
            // Selected Date Summary Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Day $selectedDay $monthName",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "${member.displayName}'s Meal Log",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Total Meals Count Badge for Selected Day
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(RadiusSm))
                        .background(DarkPrimaryGlow)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "${formatCountDisplay(countForSelectedDay)} Meals",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = DarkPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Interactive Meal Control Buttons (+ / - and preset count pills)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Decrease (-) Button
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(DarkSurface)
                        .clickable {
                            val nextCount = (countForSelectedDay - 0.5).coerceAtLeast(0.0)
                            onMealClick(member.uid, member.displayName, selectedDateIso, nextCount)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Decrease Meal",
                        tint = DarkPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Quick Count Preset Pills (0, 1.0, 2.0, 3.0)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    listOf(0.0, 1.0, 2.0, 3.0).forEach { presetCount ->
                        val isPresetSelected = countForSelectedDay == presetCount
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(RadiusSm))
                                .background(if (isPresetSelected) DarkPrimary else DarkSurface)
                                .clickable {
                                    onMealClick(member.uid, member.displayName, selectedDateIso, presetCount)
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = formatCountDisplay(presetCount),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (isPresetSelected) DarkBackground else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Increase (+) Button
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(DarkPrimary)
                        .clickable {
                            val nextCount = (countForSelectedDay + 0.5).coerceAtMost(5.0)
                            onMealClick(member.uid, member.displayName, selectedDateIso, nextCount)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase Meal",
                        tint = DarkBackground,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

private data class CalendarCellData(
    val day: Int,
    val isCurrentMonth: Boolean
)

private fun formatCountDisplay(count: Double): String {
    return when (count) {
        0.0 -> "0"
        0.5 -> "½"
        1.0 -> "1"
        1.5 -> "1½"
        2.0 -> "2"
        2.5 -> "2½"
        3.0 -> "3"
        else -> if (count % 1.0 == 0.0) "${count.toInt()}" else "$count"
    }
}
