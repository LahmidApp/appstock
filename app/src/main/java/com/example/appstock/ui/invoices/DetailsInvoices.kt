package com.example.appstock.ui.invoices

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appstock.viewmodel.InvoiceViewModel
import com.example.appstock.data.Invoice
import com.example.appstock.ui.utils.getInvoiceViewModel

@Composable
fun DetailsInvoicesScreen(
    invoiceId: Long? = null,
    invoiceViewModel: InvoiceViewModel = getInvoiceViewModel()
) {
    val allInvoices by invoiceViewModel.allInvoices.observeAsState(emptyList())
    val invoice = allInvoices.find { inv -> inv.id == invoiceId } ?: return

    var showMarkPaidDialog by remember { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Détails de la facture #${invoice.id}", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))
        Text("Client: ${if (invoice.customerId != null) "Client ID: ${invoice.customerId}" else "Client direct"}", style = MaterialTheme.typography.bodyMedium)
        Text("Montant: ${invoice.totalAmount} Dhs", style = MaterialTheme.typography.bodyMedium)
        Text("Date: ${java.text.SimpleDateFormat("dd/MM/yyyy").format(java.util.Date(invoice.dateTimestamp))}", style = MaterialTheme.typography.bodySmall)
        Text("Statut: ${if (invoice.isPaid) "Payée" else "En attente"}", style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            if (!invoice.isPaid) {
                Button(onClick = { showMarkPaidDialog = true }) { Text("Marquer comme payée") }
            }
            Button(onClick = { /* TODO: Imprimer la facture */ }) { Text("Imprimer") }
            Button(onClick = { /* TODO: Modifier la facture */ }) { Text("Modifier") }
        }
    }

    if (showMarkPaidDialog) {
        AlertDialog(
            onDismissRequest = { showMarkPaidDialog = false },
            title = { Text("Confirmer") },
            text = { Text("Marquer cette facture comme payée ?") },
            confirmButton = {
                TextButton(onClick = {
                    invoiceViewModel.update(invoice.copy(isPaid = true))
                    showMarkPaidDialog = false
                }) { Text("Oui") }
            },
            dismissButton = {
                TextButton(onClick = { showMarkPaidDialog = false }) { Text("Non") }
            }
        )
    }
}
