package com.example.appstock.ui.invoices


import android.content.Context
import android.print.PrintAttributes
import android.print.PrintManager
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appstock.viewmodel.InvoiceViewModel
import com.example.appstock.viewmodel.CompanyInfoViewModel
import com.example.appstock.data.Invoice
import com.example.appstock.data.Customer
import com.example.appstock.data.Sale
import com.example.appstock.data.CompanyInfo
import com.example.appstock.data.AppDatabase
import com.example.appstock.utils.InvoicePrintAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.appstock.ui.utils.getInvoiceViewModel
import com.example.appstock.ui.utils.getCompanyInfoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportInvoicesPdfScreen(
    invoiceViewModel: InvoiceViewModel = getInvoiceViewModel(),
    companyInfoViewModel: CompanyInfoViewModel = getCompanyInfoViewModel()
) {
    val context = LocalContext.current
    val allInvoices by invoiceViewModel.allInvoices.observeAsState(emptyList())
    val companyInfo by companyInfoViewModel.companyInfo.observeAsState()
    var search by remember { mutableStateOf("") }
    var selectedInvoice by remember { mutableStateOf<Invoice?>(null) }

    val filtered = allInvoices.filter { invoice ->
        invoice.id.toString().contains(search) || invoice.customerId.toString().contains(search)
    }

    Row(Modifier.fillMaxSize().padding(16.dp)) {
        // Liste des factures à gauche
        Column(Modifier.weight(1f).fillMaxHeight()) {
            Text("Exporter les factures en PDF", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                label = { Text("Recherche par ID ou client") },
                leadingIcon = { Icon(Icons.Filled.Search, null) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                items(filtered) { invoice ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedInvoice = invoice }
                            .background(if (selectedInvoice?.id == invoice.id) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface)
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text("Facture #${invoice.id}", style = MaterialTheme.typography.titleMedium)
                            Text("Montant: ${invoice.totalAmount} Dhs", style = MaterialTheme.typography.bodyMedium)
                            Text("Date: ${java.text.SimpleDateFormat("dd/MM/yyyy").format(java.util.Date(invoice.dateTimestamp))}", style = MaterialTheme.typography.bodySmall)
                            Text("Statut: ${if (invoice.isPaid) "Payée" else "En attente"}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
                if (filtered.isEmpty()) {
                    item { Text("Aucune facture trouvée.", modifier = Modifier.padding(16.dp)) }
                }
            }
        }

        // Preview et actions à droite
        Column(Modifier.weight(1f).fillMaxHeight().background(MaterialTheme.colorScheme.surfaceVariant), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(16.dp))
            if (selectedInvoice != null) {
                Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary)
                Text("Aperçu PDF de la facture #${selectedInvoice!!.id}", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(onClick = {
                        // Impression réelle via PrintManager
                        launchPrintPreview(context, selectedInvoice!!, companyInfo)
                    }) { Text("Imprimer / Exporter PDF") }
                    OutlinedButton(onClick = { /* TODO: Editer la facture */ }) { Text("Editer") }
                }
                Spacer(Modifier.height(24.dp))
                // Aperçu mock (texte)
                Card(Modifier.fillMaxWidth().padding(16.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Facture #${selectedInvoice!!.id}", style = MaterialTheme.typography.titleMedium)
                        Text("Montant: ${selectedInvoice!!.totalAmount} Dhs", style = MaterialTheme.typography.bodyMedium)
                        Text("Date: ${java.text.SimpleDateFormat("dd/MM/yyyy").format(java.util.Date(selectedInvoice!!.dateTimestamp))}", style = MaterialTheme.typography.bodySmall)
                        Text("Statut: ${if (selectedInvoice!!.isPaid) "Payée" else "En attente"}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            } else {
                Text("Sélectionnez une facture à exporter.", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(32.dp))
            }
        }
    }
}

fun launchPrintPreview(context: Context, invoice: Invoice, companyInfo: CompanyInfo?) {
    val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
    
    // Récupérer les données nécessaires de la base de données
    val database = AppDatabase.getDatabase(context)
    val customerDao = database.customerDao()
    val invoiceItemDao = database.invoiceItemDao()
    val productDao = database.productDao()
    
    CoroutineScope(Dispatchers.IO).launch {
        try {
            // Récupérer le client
            val customer = invoice.customerId?.let { customerId ->
                customerDao.getCustomerById(customerId)
            }
            
            // Récupérer les articles de la facture
            val invoiceItems = invoiceItemDao.getItemsForInvoiceSync(invoice.id)
            
            // Récupérer les produits correspondants
            val productIds = invoiceItems.map { it.productId }
            val products = if (productIds.isNotEmpty()) {
                productDao.getProductsByIds(productIds)
            } else {
                emptyList()
            }
            
            // Retourner au thread principal pour lancer l'impression
            withContext(Dispatchers.Main) {
                printManager.print(
                    "Facture_${invoice.id}",
                    InvoicePrintAdapter(
                        context = context,
                        invoice = invoice, 
                        customer = customer,
                        sale = null,
                        companyInfo = companyInfo,
                        invoiceItems = invoiceItems,
                        products = products
                    ),
                    PrintAttributes.Builder().build()
                )
            }
        } catch (e: Exception) {
            // En cas d'erreur, utiliser les données par défaut
            withContext(Dispatchers.Main) {
                printManager.print(
                    "Facture_${invoice.id}",
                    InvoicePrintAdapter(
                        context = context,
                        invoice = invoice, 
                        customer = null,
                        sale = null,
                        companyInfo = companyInfo,
                        invoiceItems = emptyList(),
                        products = emptyList()
                    ),
                    PrintAttributes.Builder().build()
                )
            }
        }
    }
}
