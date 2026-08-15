package com.messmanager.app.ui.grocery

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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.messmanager.app.domain.model.Grocery
import com.messmanager.app.domain.model.Member
import com.messmanager.app.ui.components.appTextFieldColors
import com.messmanager.app.ui.theme.AvatarColors
import com.messmanager.app.ui.theme.CurrencyHeroStyle
import com.messmanager.app.ui.theme.DarkBackground
import com.messmanager.app.ui.theme.DarkOutline
import com.messmanager.app.ui.theme.DarkPrimary
import com.messmanager.app.ui.theme.DarkPrimaryGlow
import com.messmanager.app.ui.theme.DarkSurface
import com.messmanager.app.ui.theme.DarkSurfaceHigh
import com.messmanager.app.ui.theme.PositiveDark
import com.messmanager.app.ui.theme.PositiveDarkBg
import com.messmanager.app.ui.theme.RadiusLg
import com.messmanager.app.ui.theme.RadiusSm
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
    var selectedGroceryForDetail by remember { mutableStateOf<Grocery?>(null) }

    if (uiState.isLoading) {
        Box(
            modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
                        text = "TOTAL GROCERY EXPENSE",
                        style = MaterialTheme.typography.labelSmall,
                        color = DarkPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        tint = DarkPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = CurrencyFormatter.formatPaisa(uiState.totalGroceryPaisa),
                    style = CurrencyHeroStyle,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${uiState.groceries.size} entries recorded this month",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Manager Inline Action Field Card
        if (uiState.isManager) {
            InlineGroceryActionCard(
                members = uiState.members,
                onAddGrocery = { itemName, costPaisa, buyerUid, buyerName ->
                    viewModel.addGrocery(
                        itemName = itemName,
                        quantity = 1.0,
                        unit = "pcs",
                        costPaisa = costPaisa,
                        buyerUid = buyerUid,
                        buyerName = buyerName,
                        date = DateUtils.todayIso(),
                        note = ""
                    )
                }
            )

            Spacer(modifier = Modifier.height(14.dp))
        }

        // Groceries List Section
        if (uiState.groceries.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No grocery entries recorded yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                itemsIndexed(uiState.groceries, key = { _, item -> item.id }) { index, grocery ->
                    val buyerColor = AvatarColors[index % AvatarColors.size]

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(RadiusLg))
                            .background(DarkSurfaceHigh)
                            .clickable {
                                selectedGroceryForDetail = grocery
                            }
                            .padding(horizontal = 14.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Buyer Avatar Initial Circle
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(buyerColor.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = grocery.buyerName.take(1).uppercase(),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = buyerColor
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = grocery.itemName,
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Text(
                            text = CurrencyFormatter.formatPaisa(grocery.costPaisa),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = DarkPrimary
                        )
                    }
                }
            }
        }
    }

    // Grocery Premium Details Modal Popup
    selectedGroceryForDetail?.let { grocery ->
        GroceryDetailModal(
            grocery = grocery,
            isManager = uiState.isManager,
            onDismiss = { selectedGroceryForDetail = null },
            onEdit = {
                selectedGroceryForDetail = null
                selectedGroceryForEdit = grocery
                showSheet = true
            },
            onDelete = {
                selectedGroceryForDetail = null
                viewModel.deleteGrocery(grocery.id)
            }
        )
    }

    // Edit Form Sheet
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InlineGroceryActionCard(
    members: List<Member>,
    onAddGrocery: (itemName: String, costPaisa: Long, buyerUid: String, buyerName: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var itemName by remember { mutableStateOf("") }
    var costText by remember { mutableStateOf("") }
    var selectedBuyer by remember { mutableStateOf(members.firstOrNull()) }
    var buyerExpanded by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val costFocusRequester = remember { FocusRequester() }

    LaunchedEffect(members) {
        if (selectedBuyer == null && members.isNotEmpty()) {
            selectedBuyer = members.firstOrNull()
        }
    }

    val submitAction = {
        val costBdt = costText.toDoubleOrNull() ?: 0.0
        val costPaisa = CurrencyFormatter.bdtToPaisa(costBdt)
        val buyer = selectedBuyer

        if (itemName.isNotBlank() && costPaisa > 0 && buyer != null) {
            onAddGrocery(
                itemName.trim(),
                costPaisa,
                buyer.uid,
                buyer.displayName
            )
            itemName = ""
            costText = ""
            focusManager.clearFocus()
        }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(DarkSurface)
            .border(BorderStroke(1.dp, DarkOutline), RoundedCornerShape(20.dp)),
        color = DarkSurface
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Card Header Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(DarkPrimaryGlow),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = DarkPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Text(
                    text = "ADD GROCERY ENTRY",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = androidx.compose.ui.unit.TextUnit(1.2f, androidx.compose.ui.unit.TextUnitType.Sp)
                    ),
                    color = DarkPrimary
                )
            }

            // Input Fields Row: Item Name & Cost
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Item Name Field
                Column(modifier = Modifier.weight(1.4f)) {
                    Text(
                        text = "Item Name",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    OutlinedTextField(
                        value = itemName,
                        onValueChange = { itemName = it },
                        placeholder = { Text("e.g. Rice, Eggs, Oil", fontSize = 13.sp) },
                        singleLine = true,
                        colors = appTextFieldColors(),
                        shape = RoundedCornerShape(RadiusSm),
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { costFocusRequester.requestFocus() }
                        )
                    )
                }

                // Cost Field
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Cost (৳)",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    OutlinedTextField(
                        value = costText,
                        onValueChange = { costText = it },
                        placeholder = { Text("650", fontSize = 13.sp) },
                        singleLine = true,
                        colors = appTextFieldColors(),
                        shape = RoundedCornerShape(RadiusSm),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(costFocusRequester),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { submitAction() }
                        )
                    )
                }
            }

            // Buyer Selector & Add Entry Button Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                // Buyer Dropdown
                Column(modifier = Modifier.weight(1.3f)) {
                    Text(
                        text = "Bought By",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    ExposedDropdownMenuBox(
                        expanded = buyerExpanded,
                        onExpandedChange = { buyerExpanded = !buyerExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedBuyer?.displayName ?: "Select Member",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = buyerExpanded) },
                            colors = appTextFieldColors(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                            shape = RoundedCornerShape(RadiusSm)
                        )
                        ExposedDropdownMenu(
                            expanded = buyerExpanded,
                            onDismissRequest = { buyerExpanded = false }
                        ) {
                            members.forEach { m ->
                                DropdownMenuItem(
                                    text = { Text(m.displayName) },
                                    onClick = {
                                        selectedBuyer = m
                                        buyerExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Add Entry Button
                Button(
                    onClick = { submitAction() },
                    enabled = itemName.isNotBlank() && costText.isNotBlank() && selectedBuyer != null,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(RadiusSm),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkPrimary,
                        contentColor = DarkBackground,
                        disabledContainerColor = DarkSurfaceHigh,
                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text(
                        text = "Add Entry",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }
}

@Composable
private fun GroceryDetailModal(
    grocery: Grocery,
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
                        text = "GROCERY DETAILS",
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

                // Item Name & Buyer Avatar Block
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
                            text = grocery.buyerName.take(1).uppercase(),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = DarkPrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = grocery.itemName,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Purchased by ${grocery.buyerName}",
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
                            text = "ITEM EXPENSE",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = DarkPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = CurrencyFormatter.formatPaisa(grocery.costPaisa),
                            style = CurrencyHeroStyle,
                            color = DarkPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Details Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(RadiusLg))
                        .background(DarkSurfaceHigh)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DetailRow(
                        label = "Quantity / Weight",
                        value = "${grocery.quantity} ${grocery.unit}"
                    )

                    DetailRow(
                        label = "Purchased By",
                        value = grocery.buyerName
                    )

                    DetailRow(
                        label = "Date Recorded",
                        value = DateUtils.formatDisplay(grocery.date)
                    )

                    if (grocery.note.isNotEmpty()) {
                        DetailRow(
                            label = "Note",
                            value = grocery.note
                        )
                    }

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
                                    text = "CONFIRMED EXPENSE",
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
