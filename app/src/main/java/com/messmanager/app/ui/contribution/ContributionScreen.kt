package com.messmanager.app.ui.contribution

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.messmanager.app.domain.model.Contribution
import com.messmanager.app.ui.theme.AvatarColors
import com.messmanager.app.ui.theme.CurrencyHeroStyle
import com.messmanager.app.ui.theme.DarkBackground
import com.messmanager.app.ui.theme.DarkOutline
import com.messmanager.app.ui.theme.DarkPrimary
import com.messmanager.app.ui.theme.DarkPrimaryGlow
import com.messmanager.app.ui.theme.DarkSecondary
import com.messmanager.app.ui.theme.DarkSurface
import com.messmanager.app.ui.theme.DarkSurfaceHigh
import com.messmanager.app.ui.theme.PositiveDark
import com.messmanager.app.ui.theme.PositiveDarkBg
import com.messmanager.app.ui.theme.RadiusLg
import com.messmanager.app.ui.theme.RadiusSm
import com.messmanager.app.util.CurrencyFormatter
import com.messmanager.app.util.DateUtils

@Composable
fun ContributionScreen(
    viewModel: ContributionViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var showSheet by remember { mutableStateOf(false) }
    var selectedContributionForEdit by remember { mutableStateOf<Contribution?>(null) }
    var selectedContributionForDetail by remember { mutableStateOf<Contribution?>(null) }

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
            if (uiState.isManager) {
                FloatingActionButton(
                    onClick = {
                        selectedContributionForEdit = null
                        showSheet = true
                    },
                    containerColor = DarkSecondary,
                    contentColor = MaterialTheme.colorScheme.background
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Deposit")
                }
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
            // Total Header Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(DarkPrimaryGlow.copy(alpha = 0.35f))
                    .border(BorderStroke(1.dp, DarkPrimary.copy(alpha = 0.35f)), RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TOTAL MEMBER DEPOSITS",
                            style = MaterialTheme.typography.labelSmall,
                            color = DarkPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = DarkPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = CurrencyFormatter.formatPaisa(uiState.totalContributionPaisa),
                        style = CurrencyHeroStyle,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Total cash collected by the Mess Manager",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.contributions.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No member deposits recorded yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(20.dp))
                        .background(DarkSurface)
                        .border(BorderStroke(1.dp, DarkOutline), RoundedCornerShape(20.dp))
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(uiState.contributions, key = { _, item -> item.id }) { index, contribution ->
                        val avatarColor = AvatarColors[index % AvatarColors.size]

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(RadiusLg))
                                .background(DarkSurfaceHigh)
                                .clickable {
                                    selectedContributionForDetail = contribution
                                }
                                .padding(horizontal = 14.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(avatarColor.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = contribution.memberName.take(1).uppercase(),
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = avatarColor
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Text(
                                    text = contribution.memberName,
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Text(
                                text = CurrencyFormatter.formatPaisa(contribution.amountPaisa),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = DarkPrimary
                            )
                        }
                    }
                }
            }
        }

        // Premium Deposit Detail Popup Modal
        selectedContributionForDetail?.let { contribution ->
            DepositDetailModal(
                contribution = contribution,
                isManager = uiState.isManager,
                onDismiss = { selectedContributionForDetail = null },
                onEdit = {
                    selectedContributionForDetail = null
                    selectedContributionForEdit = contribution
                    showSheet = true
                },
                onDelete = {
                    selectedContributionForDetail = null
                    viewModel.deleteContribution(contribution.id)
                }
            )
        }

        // Deposit Add/Edit Form Sheet
        if (showSheet) {
            ContributionFormSheet(
                members = uiState.members,
                existingContribution = selectedContributionForEdit,
                onSave = { memberUid, memberName, amountPaisa, date, purpose ->
                    if (selectedContributionForEdit == null) {
                        viewModel.addContribution(memberUid, memberName, amountPaisa, date, purpose)
                    } else {
                        viewModel.updateContribution(
                            selectedContributionForEdit!!.copy(
                                memberUid = memberUid,
                                memberName = memberName,
                                amountPaisa = amountPaisa,
                                date = date,
                                purpose = purpose
                            )
                        )
                    }
                    showSheet = false
                },
                onDismiss = { showSheet = false }
            )
        }
    }
}

@Composable
private fun DepositDetailModal(
    contribution: Contribution,
    isManager: Boolean,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(24.dp))
                .background(DarkSurface)
                .border(BorderStroke(1.dp, DarkOutline), RoundedCornerShape(24.dp)),
            color = DarkSurface
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Header Title with Close X Icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "DEPOSIT DETAILS",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = androidx.compose.ui.unit.TextUnit(1.5f, androidx.compose.ui.unit.TextUnitType.Sp)
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(DarkSurfaceHigh)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Member Avatar & Name Block
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(DarkPrimaryGlow),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = contribution.memberName.take(1).uppercase(),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = DarkPrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = contribution.memberName,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Mess Member",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Hero Amount Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(RadiusLg))
                        .background(DarkPrimaryGlow.copy(alpha = 0.4f))
                        .border(BorderStroke(1.dp, DarkPrimary.copy(alpha = 0.4f)), RoundedCornerShape(RadiusLg))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "DEPOSITED AMOUNT",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = DarkPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = CurrencyFormatter.formatPaisa(contribution.amountPaisa),
                            style = CurrencyHeroStyle,
                            color = DarkPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Metadata Details Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(RadiusLg))
                        .background(DarkSurfaceHigh)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DetailRow(
                        label = "Type / Category",
                        value = contribution.purpose.ifBlank { "Monthly Deposit" }
                    )

                    DetailRow(
                        label = "Date Recorded",
                        value = DateUtils.formatDisplay(contribution.date)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Status",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(RadiusSm))
                                .background(PositiveDarkBg)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = PositiveDark,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "CONFIRMED",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = PositiveDark
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Actions
                if (isManager) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDelete,
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            shape = RoundedCornerShape(RadiusLg),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Delete", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = onEdit,
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                            shape = RoundedCornerShape(RadiusLg),
                            colors = ButtonDefaults.buttonColors(containerColor = DarkPrimary)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = DarkBackground, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Edit", color = DarkBackground, fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        shape = RoundedCornerShape(RadiusLg),
                        colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceHigh)
                    ) {
                        Text("DONE", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
