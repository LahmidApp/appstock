package com.example.appstock.ui.invoices

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
fun PaidInvoicesScreen(invoiceViewModel: InvoiceViewModel = getInvoiceViewModel()) {
    val paidInvoices by invoiceViewModel.allInvoices.observeAsState(emptyList())
    val filteredPaid = paidInvoices.filter { invoice -> invoice.isPaid }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Factures payées", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(filteredPaid) { invoice ->
                PaidInvoiceItem(invoice = invoice, onClick = { /* TODO: Afficher détails/options */ })
            }
            if (filteredPaid.isEmpty()) {
                item { Text("Aucune facture payée.", modifier = Modifier.padding(16.dp)) }
            }
        }
    }
}

@Composable
fun PaidInvoiceItem(invoice: Invoice, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Facture #${invoice.id}", style = MaterialTheme.typography.titleMedium)
            Text("Montant: ${invoice.totalAmount} Dhs", style = MaterialTheme.typography.bodyMedium)
            Text("Date: ${java.text.SimpleDateFormat("dd/MM/yyyy").format(java.util.Date(invoice.dateTimestamp))}", style = MaterialTheme.typography.bodySmall)
            Text("Statut: Payée", style = MaterialTheme.typography.bodySmall)
        }
    }
}
