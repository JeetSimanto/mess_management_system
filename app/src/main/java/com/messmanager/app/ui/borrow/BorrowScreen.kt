package com.messmanager.app.ui.borrow

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.messmanager.app.domain.model.BorrowRequest
import com.messmanager.app.domain.model.BorrowStatus
import com.messmanager.app.ui.theme.DarkPrimary
import com.messmanager.app.ui.theme.DarkPrimaryGlow
import com.messmanager.app.ui.theme.DarkSecondary
import com.messmanager.app.ui.theme.DarkSurface
import com.messmanager.app.ui.theme.DarkSurfaceHigh
import com.messmanager.app.ui.theme.NegativeDark
import com.messmanager.app.ui.theme.NegativeDarkBg
import com.messmanager.app.ui.theme.PositiveDark
import com.messmanager.app.ui.theme.PositiveDarkBg
import com.messmanager.app.ui.theme.RadiusLg
import com.messmanager.app.ui.theme.RadiusSm
import com.messmanager.app.util.DateUtils

@Composable
fun BorrowScreen(
    viewModel: BorrowViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var showRequestSheet by remember { mutableStateOf(false) }
    var selectedBorrowForAccept by remember { mutableStateOf<BorrowRequest?>(null) }

    if (uiState.isLoading) {
        Box(
            modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showRequestSheet = true },
                containerColor = DarkSecondary,
                contentColor = MaterialTheme.colorScheme.background
            ) {
                Icon(Icons.Default.Add, contentDescription = "Request Borrow")
            }
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(16.dp)
        ) {
            // Notice Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(RadiusLg))
                    .background(DarkPrimaryGlow)
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(DarkPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = "Notice",
                            tint = DarkPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Item Return Policy",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = DarkPrimary
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "The manager will not accept any cash for physical items. Borrowed items must be returned physically in the agreed timeframe.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.borrowRequests.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No item borrow requests.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(RadiusLg))
                        .background(DarkSurface)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.borrowRequests, key = { it.id }) { borrow ->
                        BorrowItemRow(
                            borrow = borrow,
                            isManager = uiState.isManager,
                            onAcceptClick = { selectedBorrowForAccept = borrow },
                            onRejectClick = { viewModel.resolveBorrow(borrow.id, accept = false) },
                            onMarkReturned = { viewModel.markReturned(borrow.id) }
                        )
                    }
                }
            }
        }

        if (showRequestSheet) {
            BorrowRequestSheet(
                onSend = { itemName, quantity, date ->
                    viewModel.sendBorrowRequest(itemName, quantity, date)
                    showRequestSheet = false
                },
                onDismiss = { showRequestSheet = false }
            )
        }

        selectedBorrowForAccept?.let { borrow ->
            BorrowAcceptDialog(
                borrowRequest = borrow,
                onConfirm = { dueDate ->
                    viewModel.resolveBorrow(borrow.id, accept = true, dueDate = dueDate)
                    selectedBorrowForAccept = null
                },
                onDismiss = { selectedBorrowForAccept = null }
            )
        }
    }
}

@Composable
private fun BorrowItemRow(
    borrow: BorrowRequest,
    isManager: Boolean,
    onAcceptClick: () -> Unit,
    onRejectClick: () -> Unit,
    onMarkReturned: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(RadiusLg))
            .background(DarkSurfaceHigh)
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(DarkSecondary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Handshake,
                        contentDescription = null,
                        tint = DarkSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "${borrow.itemName} (${borrow.quantity})",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Borrowed by ${borrow.requesterName} on ${DateUtils.formatDisplay(borrow.date)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (borrow.dueDate.isNotEmpty() && borrow.status == BorrowStatus.ACCEPTED) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Due Date: ${DateUtils.formatDisplay(borrow.dueDate)} (Daily Reminder Active)",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            color = DarkSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Status Badge
            val (statusText, statusBg, statusColor) = when (borrow.status) {
                BorrowStatus.PENDING -> Triple("PENDING", DarkSurfaceHigh, DarkSecondary)
                BorrowStatus.ACCEPTED -> Triple("ACCEPTED", DarkPrimaryGlow, DarkPrimary)
                BorrowStatus.REJECTED -> Triple("REJECTED", NegativeDarkBg, NegativeDark)
                BorrowStatus.RETURNED -> Triple("RETURNED", PositiveDarkBg, PositiveDark)
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(RadiusSm))
                    .background(statusBg)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = statusColor
                )
            }
        }

        // Manager Actions Row
        if (isManager) {
            when (borrow.status) {
                BorrowStatus.PENDING -> {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(onClick = onRejectClick) {
                            Text("Reject", color = MaterialTheme.colorScheme.error)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = onAcceptClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DarkPrimary,
                                contentColor = MaterialTheme.colorScheme.background
                            )
                        ) {
                            Text("Accept & Set Due", fontWeight = FontWeight.Bold)
                        }
                    }
                }
                BorrowStatus.ACCEPTED -> {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = onMarkReturned,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PositiveDark,
                                contentColor = MaterialTheme.colorScheme.background
                            )
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Mark as Returned", fontWeight = FontWeight.Bold)
                        }
                    }
                }
                else -> {}
            }
        }
    }
}
