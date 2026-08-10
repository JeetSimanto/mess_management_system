package com.messmanager.app.ui.borrow

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.messmanager.app.domain.model.BorrowRequest
import com.messmanager.app.ui.components.appTextFieldColors
import com.messmanager.app.ui.theme.RadiusLg
import com.messmanager.app.util.DateUtils

@Composable
fun BorrowAcceptDialog(
    borrowRequest: BorrowRequest,
    onConfirm: (dueDate: String) -> Unit,
    onDismiss: () -> Unit
) {
    var dueDate by remember { mutableStateOf(DateUtils.todayIso()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Approve Borrow Request",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column {
                Text(
                    text = "${borrowRequest.requesterName} wants to borrow ${borrowRequest.quantity} of ${borrowRequest.itemName}.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = dueDate,
                    onValueChange = { dueDate = it },
                    label = { Text("Return Due Date (YYYY-MM-DD)") },
                    singleLine = true,
                    colors = appTextFieldColors(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(RadiusLg)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(dueDate.trim()) },
                enabled = dueDate.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.background
                )
            ) {
                Text("Approve & Set Due Date", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
