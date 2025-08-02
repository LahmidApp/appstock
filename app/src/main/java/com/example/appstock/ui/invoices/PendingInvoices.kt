package com.example.appstock.ui.invoices

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appstock.viewmodel.InvoiceViewModel
import com.example.appstock.data.Invoice
import com.example.appstock.ui.utils.getInvoiceViewModel

@Composable
fun PendingInvoicesScreen(invoiceViewModel: InvoiceViewModel = getInvoiceViewModel()) {
    val pendingInvoices by invoiceViewModel.pendingInvoices.observeAsState(emptyList())

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Factures en attente", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(pendingInvoices) { invoice ->
                PendingInvoiceItem(
                    invoice = invoice, 
                    onMarkAsPaid = { 
                        // Marquer la facture comme payée
                        val updatedInvoice = invoice.copy(isPaid = true)
                        invoiceViewModel.update(updatedInvoice)
                    },
                    onClick = { /* TODO: Afficher détails/options */ }
                )
            }
            if (pendingInvoices.isEmpty()) {
                item { Text("Aucune facture en attente.", modifier = Modifier.padding(16.dp)) }
            }
        }
    }
}

@Composable
fun PendingInvoiceItem(
    invoice: Invoice, 
    onMarkAsPaid: () -> Unit,
    onClick: () -> Unit
) {
    var showConfirmDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Facture #${invoice.id}", style = MaterialTheme.typography.titleMedium)
                Text("Montant: ${String.format("%.2f", invoice.totalAmount)} Dhs", 
                     style = MaterialTheme.typography.bodyMedium, 
                     fontWeight = FontWeight.Medium)
                Text("Date: ${java.text.SimpleDateFormat("dd/MM/yyyy").format(java.util.Date(invoice.dateTimestamp))}", 
                     style = MaterialTheme.typography.bodySmall)
                Text("Statut: En attente", 
                     style = MaterialTheme.typography.bodySmall,
                     color = MaterialTheme.colorScheme.error)
            }
            
            // Actions
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = { showConfirmDialog = true },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Marquer comme payée",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                
                IconButton(onClick = onClick) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Modifier",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }

    // Dialog de confirmation
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Confirmer le paiement") },
            text = { 
                Text("Êtes-vous sûr de vouloir marquer cette facture comme payée ?\n\nFacture #${invoice.id}\nMontant: ${String.format("%.2f", invoice.totalAmount)} Dhs")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onMarkAsPaid()
                        showConfirmDialog = false
                    }
                ) {
                    Text("Confirmer")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }
}
