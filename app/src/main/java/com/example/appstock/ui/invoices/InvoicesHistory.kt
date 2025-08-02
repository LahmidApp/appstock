package com.example.appstock.ui.invoices

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appstock.viewmodel.InvoiceViewModel
import com.example.appstock.data.Invoice
import com.example.appstock.ui.utils.getInvoiceViewModel

@Composable
fun InvoicesHistoryScreen(invoiceViewModel: InvoiceViewModel = getInvoiceViewModel()) {
    val allInvoices by invoiceViewModel.allInvoices.observeAsState(emptyList())
    var search by remember { mutableStateOf("") }
    var showPaidOnly by remember { mutableStateOf(false) }

    val filtered = allInvoices.filter { invoice ->
        (invoice.id.toString().contains(search) || invoice.customerId.toString().contains(search)) &&
        (!showPaidOnly || invoice.isPaid)
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Historique des factures", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                label = { Text("Recherche par ID ou client") },
                leadingIcon = { Icon(Icons.Filled.Search, null) },
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            FilterChip(
                selected = showPaidOnly,
                onClick = { showPaidOnly = !showPaidOnly },
                label = { Text("Payées seulement") }
            )
        }
        Spacer(Modifier.height(16.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(filtered) { invoice ->
                HistoryInvoiceItem(invoice = invoice, onClick = { /* TODO: Détails */ })
            }
            if (filtered.isEmpty()) {
                item { Text("Aucune facture trouvée.", modifier = Modifier.padding(16.dp)) }
            }
        }
    }
}

@Composable
fun HistoryInvoiceItem(invoice: Invoice, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Facture #${invoice.id}", style = MaterialTheme.typography.titleMedium)
            Text("Montant: ${invoice.totalAmount} Dhs", style = MaterialTheme.typography.bodyMedium)
            Text("Date: ${java.text.SimpleDateFormat("dd/MM/yyyy").format(java.util.Date(invoice.dateTimestamp))}", style = MaterialTheme.typography.bodySmall)
            Text("Statut: ${if (invoice.isPaid) "Payée" else "En attente"}", style = MaterialTheme.typography.bodySmall)
        }
    }
}
