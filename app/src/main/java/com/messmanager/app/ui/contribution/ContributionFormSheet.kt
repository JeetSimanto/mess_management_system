package com.messmanager.app.ui.contribution

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
import com.messmanager.app.domain.model.Contribution
import com.messmanager.app.domain.model.Member
import com.messmanager.app.ui.theme.RadiusLg
import com.messmanager.app.util.CurrencyFormatter
import com.messmanager.app.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContributionFormSheet(
    members: List<Member>,
    existingContribution: Contribution? = null,
    onSave: (memberUid: String, memberName: String, amountPaisa: Long, date: String, purpose: String) -> Unit,
    onDismiss: () -> Unit
) {
    var amountText by remember { mutableStateOf(existingContribution?.amountBdt?.toString() ?: "") }
    var purpose by remember { mutableStateOf(existingContribution?.purpose ?: "Monthly Deposit") }
    var date by remember { mutableStateOf(existingContribution?.date ?: DateUtils.todayIso()) }

    var selectedMember by remember {
        mutableStateOf(
            members.find { it.uid == existingContribution?.memberUid } ?: members.firstOrNull()
        )
    }

    var memberExpanded by remember { mutableStateOf(false) }

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
                text = if (existingContribution == null) "Record Member Deposit" else "Edit Member Deposit",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Member Selector Dropdown
            ExposedDropdownMenuBox(
                expanded = memberExpanded,
                onExpandedChange = { memberExpanded = !memberExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedMember?.displayName ?: "Select Member",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Deposited By") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = memberExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                    shape = RoundedCornerShape(RadiusLg)
                )
                ExposedDropdownMenu(
                    expanded = memberExpanded,
                    onDismissRequest = { memberExpanded = false }
                ) {
                    members.forEach { m ->
                        DropdownMenuItem(
                            text = { Text(m.displayName) },
                            onClick = {
                                selectedMember = m
                                memberExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it },
                label = { Text("Deposit Amount (BDT ৳)") },
                placeholder = { Text("3000") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(RadiusLg)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = purpose,
                onValueChange = { purpose = it },
                label = { Text("Purpose / Note") },
                placeholder = { Text("e.g. Monthly Deposit / Initial Fund") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(RadiusLg)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val amountBdt = amountText.toDoubleOrNull() ?: 0.0
                    val amountPaisa = CurrencyFormatter.bdtToPaisa(amountBdt)
                    val member = selectedMember

                    if (amountText.isNotBlank() && member != null) {
                        onSave(member.uid, member.displayName, amountPaisa, date, purpose.trim())
                    }
                },
                enabled = amountText.isNotBlank() && selectedMember != null,
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
                    text = if (existingContribution == null) "Save Deposit" else "Update Deposit",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
