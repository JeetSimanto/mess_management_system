package com.messmanager.app.ui.grocery

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.messmanager.app.domain.model.Grocery
import com.messmanager.app.domain.model.Member
import com.messmanager.app.ui.components.appTextFieldColors
import com.messmanager.app.ui.theme.RadiusLg
import com.messmanager.app.util.CurrencyFormatter
import com.messmanager.app.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroceryFormSheet(
    members: List<Member>,
    existingGrocery: Grocery? = null,
    onSave: (itemName: String, quantity: Double, unit: String, costPaisa: Long, buyerUid: String, buyerName: String, date: String, note: String) -> Unit,
    onDismiss: () -> Unit
) {
    var itemName by remember { mutableStateOf(existingGrocery?.itemName ?: "") }
    var quantityText by remember { mutableStateOf(existingGrocery?.quantity?.toString() ?: "") }
    var unit by remember { mutableStateOf(existingGrocery?.unit ?: "kg") }
    var costText by remember { mutableStateOf(existingGrocery?.costBdt?.toString() ?: "") }
    var note by remember { mutableStateOf(existingGrocery?.note ?: "") }
    var date by remember { mutableStateOf(existingGrocery?.date ?: DateUtils.todayIso()) }

    var selectedBuyer by remember {
        mutableStateOf(
            members.find { it.uid == existingGrocery?.buyerUid } ?: members.firstOrNull()
        )
    }

    var buyerExpanded by remember { mutableStateOf(false) }
    var unitExpanded by remember { mutableStateOf(false) }

    val units = listOf("kg", "gm", "liter", "pcs", "packet", "box")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = if (existingGrocery == null) "Add Grocery Purchase" else "Edit Grocery Purchase",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = itemName,
                onValueChange = { itemName = it },
                label = { Text("Item Name") },
                placeholder = { Text("e.g. Rice / Eggs / Oil") },
                singleLine = true,
                colors = appTextFieldColors(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(RadiusLg)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = quantityText,
                    onValueChange = { quantityText = it },
                    label = { Text("Quantity") },
                    placeholder = { Text("5") },
                    singleLine = true,
                    colors = appTextFieldColors(),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(RadiusLg)
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Unit Selector
                ExposedDropdownMenuBox(
                    expanded = unitExpanded,
                    onExpandedChange = { unitExpanded = !unitExpanded },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = unit,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Unit") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = unitExpanded) },
                        colors = appTextFieldColors(),
                        modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        shape = RoundedCornerShape(RadiusLg)
                    )
                    ExposedDropdownMenu(
                        expanded = unitExpanded,
                        onDismissRequest = { unitExpanded = false }
                    ) {
                        units.forEach { u ->
                            DropdownMenuItem(
                                text = { Text(u) },
                                onClick = {
                                    unit = u
                                    unitExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = costText,
                onValueChange = { costText = it },
                label = { Text("Cost (BDT ৳)") },
                placeholder = { Text("650") },
                singleLine = true,
                colors = appTextFieldColors(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(RadiusLg)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Buyer Selector Dropdown
            ExposedDropdownMenuBox(
                expanded = buyerExpanded,
                onExpandedChange = { buyerExpanded = !buyerExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedBuyer?.displayName ?: "Select Buyer",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Bought By") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = buyerExpanded) },
                    colors = appTextFieldColors(),
                    modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                    shape = RoundedCornerShape(RadiusLg)
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

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Note (Optional)") },
                placeholder = { Text("e.g. Bought from Minabazar") },
                colors = appTextFieldColors(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(RadiusLg)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val quantity = quantityText.toDoubleOrNull() ?: 1.0
                    val costBdt = costText.toDoubleOrNull() ?: 0.0
                    val costPaisa = CurrencyFormatter.bdtToPaisa(costBdt)
                    val buyer = selectedBuyer

                    if (itemName.isNotBlank() && buyer != null) {
                        onSave(
                            itemName.trim(),
                            quantity,
                            unit,
                            costPaisa,
                            buyer.uid,
                            buyer.displayName,
                            date,
                            note.trim()
                        )
                    }
                },
                enabled = itemName.isNotBlank() && costText.isNotBlank() && selectedBuyer != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(RadiusLg),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.background
                )
            ) {
                Text(
                    text = if (existingGrocery == null) "Save Grocery" else "Update Grocery",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
