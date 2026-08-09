package com.messmanager.app.ui.grocery

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.messmanager.app.domain.model.Grocery
import com.messmanager.app.ui.theme.CurrencyHeroStyle
import com.messmanager.app.ui.theme.DarkPrimaryGlow
import com.messmanager.app.ui.theme.DarkSurface
import com.messmanager.app.ui.theme.RadiusLg
import com.messmanager.app.util.CurrencyFormatter
import com.messmanager.app.util.DateUtils

@Composable
fun GroceryScreen(
    viewModel: GroceryViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    var showSheet by remember { mutableStateOf(false) }
    var selectedGroceryForEdit by remember { mutableStateOf<Grocery?>(null) }

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
                        selectedGroceryForEdit = null
                        showSheet = true
                    },
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.background
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Grocery")
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
                    .clip(RoundedCornerShape(RadiusLg))
                    .background(DarkPrimaryGlow)
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        text = "TOTAL GROCERY EXPENSE",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = CurrencyFormatter.formatPaisa(uiState.totalGroceryPaisa),
                        style = CurrencyHeroStyle,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Text(
                        text = "${uiState.groceries.size} entries recorded this month",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.groceries.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No grocery entries yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                    items(uiState.groceries, key = { it.id }) { grocery ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = uiState.isManager) {
                                    selectedGroceryForEdit = grocery
                                    showSheet = true
                                },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = grocery.itemName,
                                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${grocery.quantity} ${grocery.unit} · Bought by ${grocery.buyerName} (${DateUtils.formatDisplay(grocery.date)})",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (grocery.note.isNotEmpty()) {
                                    Text(
                                        text = grocery.note,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceDim
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = CurrencyFormatter.formatPaisa(grocery.costPaisa),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.primary
                                )

                                if (uiState.isManager) {
                                    IconButton(onClick = { viewModel.deleteGrocery(grocery.id) }) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    }
                }
            }
        }

        if (showSheet) {
            GroceryFormSheet(
                members = uiState.members,
                existingGrocery = selectedGroceryForEdit,
                onSave = { itemName, quantity, unit, costPaisa, buyerUid, buyerName, date, note ->
                    if (selectedGroceryForEdit == null) {
                        viewModel.addGrocery(itemName, quantity, unit, costPaisa, buyerUid, buyerName, date, note)
                    } else {
                        viewModel.updateGrocery(
                            selectedGroceryForEdit!!.copy(
                                itemName = itemName,
                                quantity = quantity,
                                unit = unit,
                                costPaisa = costPaisa,
                                buyerUid = buyerUid,
                                buyerName = buyerName,
                                date = date,
                                note = note
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
