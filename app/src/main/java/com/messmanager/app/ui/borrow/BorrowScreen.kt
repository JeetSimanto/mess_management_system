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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
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
import com.messmanager.app.ui.theme.DarkPrimaryGlow
import com.messmanager.app.ui.theme.DarkSurface
import com.messmanager.app.ui.theme.DarkSurfaceHigh
import com.messmanager.app.ui.theme.PositiveDark
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
                containerColor = MaterialTheme.colorScheme.secondary,
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
            // Notice Card as requested by user
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(RadiusLg))
                    .background(DarkPrimaryGlow)
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = "Notice",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    Spacer(modifier = Modifier.padding(horizontal = 6.dp))

                    Column {
                        Text(
                            text = "Item Return Policy",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "The manager will not accept any money, what you have borrowed needs to be returned in the given time period.",
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
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.borrowRequests, key = { it.id }) { borrow ->
                        BorrowItemRow(
                            borrow = borrow,
                            isManager = uiState.isManager,
                            onAcceptClick = { selectedBorrowForAccept = borrow },
                            onRejectClick = { viewModel.resolveBorrow(borrow.id, accept = false) },
                            onMarkReturned = { viewModel.markReturned(borrow.id) }
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
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
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
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
                    Text(
                        text = "Due Date: ${DateUtils.formatDisplay(borrow.dueDate)} (Daily Reminder Active)",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            // Status Badge
            val (statusText, statusBg, statusColor) = when (borrow.status) {
                BorrowStatus.PENDING -> Triple("PENDING", DarkSurfaceHigh, MaterialTheme.colorScheme.secondary)
                BorrowStatus.ACCEPTED -> Triple("ACCEPTED", DarkPrimaryGlow, MaterialTheme.colorScheme.primary)
                BorrowStatus.REJECTED -> Triple("REJECTED", MaterialTheme.colorScheme.errorContainer, MaterialTheme.colorScheme.error)
                BorrowStatus.RETURNED -> Triple("RETURNED", DarkSurfaceHigh, PositiveDark)
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
            Spacer(modifier = Modifier.height(8.dp))

            when (borrow.status) {
                BorrowStatus.PENDING -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(onClick = onRejectClick) {
                            Text("Reject", color = MaterialTheme.colorScheme.error)
                        }

                        Spacer(modifier = Modifier.padding(horizontal = 4.dp))

                        Button(
                            onClick = onAcceptClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.background
                            )
                        ) {
                            Text("Accept", fontWeight = FontWeight.Bold)
                        }
                    }
                }
                BorrowStatus.ACCEPTED -> {
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
                            Icon(Icons.Default.CheckCircle, contentDescription = null)
                            Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                            Text("Mark as Returned", fontWeight = FontWeight.Bold)
                        }
                    }
                }
                else -> {}
            }
        }
    }
}
