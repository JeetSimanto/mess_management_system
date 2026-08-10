package com.messmanager.app.ui.utility

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.messmanager.app.domain.model.Utility
import com.messmanager.app.ui.components.appTextFieldColors
import com.messmanager.app.ui.theme.RadiusLg
import com.messmanager.app.util.CurrencyFormatter
import com.messmanager.app.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UtilityFormSheet(
    existingUtility: Utility? = null,
    onSave: (title: String, category: String, costPaisa: Long, date: String) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf(existingUtility?.title ?: "") }
    var category by remember { mutableStateOf(existingUtility?.category ?: "Rent") }
    var costText by remember { mutableStateOf(existingUtility?.costBdt?.toString() ?: "") }
    var date by remember { mutableStateOf(existingUtility?.date ?: DateUtils.todayIso()) }

    var categoryExpanded by remember { mutableStateOf(false) }
    val categories = listOf("Rent", "Electricity", "Gas", "Water", "Waste", "Transport", "WiFi", "Other")

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
                text = if (existingUtility == null) "Add Utility Bill" else "Edit Utility Bill",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                placeholder = { Text("e.g. House Rent / Desco Electricity") },
                singleLine = true,
                colors = appTextFieldColors(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(RadiusLg)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Category Selector
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = !categoryExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    colors = appTextFieldColors(),
                    modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                    shape = RoundedCornerShape(RadiusLg)
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat) },
                            onClick = {
                                category = cat
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = costText,
                onValueChange = { costText = it },
                label = { Text("Cost (BDT ৳)") },
                placeholder = { Text("12000") },
                singleLine = true,
                colors = appTextFieldColors(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(RadiusLg)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val costBdt = costText.toDoubleOrNull() ?: 0.0
                    val costPaisa = CurrencyFormatter.bdtToPaisa(costBdt)

                    if (title.isNotBlank()) {
                        onSave(title.trim(), category, costPaisa, date)
                    }
                },
                enabled = title.isNotBlank() && costText.isNotBlank(),
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
                    text = if (existingUtility == null) "Save Bill" else "Update Bill",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
