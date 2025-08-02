package com.example.appstock.ui.screens

import android.content.Context
import android.print.PrintAttributes
import android.print.PrintManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Transform
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.appstock.data.AppDatabase
import com.example.appstock.data.InvoiceItem
import com.example.appstock.ui.utils.getQuoteViewModel
import com.example.appstock.ui.utils.getCustomerViewModel
import com.example.appstock.ui.utils.getProductViewModel
import com.example.appstock.ui.utils.getInvoiceViewModel
import com.example.appstock.ui.utils.getCompanyInfoViewModel
import com.example.appstock.data.Quote
import com.example.appstock.data.Customer
import com.example.appstock.data.Product
import com.example.appstock.data.Invoice
import com.example.appstock.data.CompanyInfo
import com.example.appstock.ui.components.StatCard
import com.example.appstock.utils.QuotePrintAdapter
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuotesScreen() {
    val context = LocalContext.current
    val quoteViewModel = getQuoteViewModel()
    val customerViewModel = getCustomerViewModel()
    val productViewModel = getProductViewModel()
    val invoiceViewModel = getInvoiceViewModel()
    val companyInfoViewModel = getCompanyInfoViewModel()
    
    val allQuotes by quoteViewModel.allQuotes.observeAsState(emptyList())
    val allCustomers by customerViewModel.allCustomers.observeAsState(emptyList())
    val allProducts by productViewModel.allProducts.observeAsState(emptyList())
    val companyInfo by companyInfoViewModel.companyInfo.observeAsState()
    
    var showNewQuoteDialog by remember { mutableStateOf(false) }
    var selectedQuote by remember { mutableStateOf<Quote?>(null) }
    var showQuoteActions by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Devis",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            FloatingActionButton(
                onClick = { showNewQuoteDialog = true },
                modifier = Modifier.size(56.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Nouveau devis")
            }
        }

        // Statistics cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCard(
                title = "Total",
                value = allQuotes.size.toString(),
                icon = Icons.Default.Description,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "En attente",
                value = allQuotes.count { it.status == "pending" }.toString(),
                icon = Icons.Default.Schedule,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "Acceptés",
                value = allQuotes.count { it.status == "accepted" }.toString(),
                icon = Icons.Default.CheckCircle,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick actions
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                QuoteActionCard(
                    title = "Créer un nouveau devis",
                    description = "Créer un devis pour un client",
                    icon = Icons.Filled.Add,
                    onClick = { showNewQuoteDialog = true }
                )
            }
            
            item {
                QuoteActionCard(
                    title = "Devis en attente",
                    description = "Voir les devis en attente de validation",
                    icon = Icons.Filled.Schedule,
                    onClick = { /* Filter pending quotes */ }
                )
            }
            
            item {
                QuoteActionCard(
                    title = "Exporter en PDF",
                    description = "Générer un PDF professionnel du devis",
                    icon = Icons.Filled.PictureAsPdf,
                    onClick = { /* Show export quote dialog */ }
                )
            }
            
            item {
                QuoteActionCard(
                    title = "Convertir en facture",
                    description = "Transformer un devis accepté en facture",
                    icon = Icons.Filled.Transform,
                    onClick = { /* Show convertible quotes */ }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Quotes list
        Text(
            text = "Liste des devis",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(allQuotes.sortedByDescending { it.dateTimestamp }) { quote ->
                QuoteListItem(
                    quote = quote,
                    customer = allCustomers.find { it.id == quote.customerId },
                    onClick = { 
                        selectedQuote = quote
                        showQuoteActions = true
                    }
                )
            }
            
            if (allQuotes.isEmpty()) {
                item {
                    Card {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Description, contentDescription = null)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Aucun devis créé")
                            Text("Créez votre premier devis !", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }

    // New quote dialog
    if (showNewQuoteDialog) {
        CreateQuoteDialog(
            customers = allCustomers,
            products = allProducts,
            onDismiss = { showNewQuoteDialog = false },
            onConfirm = { customerId, productId, quantity, unitPrice, description ->
                val quote = Quote(
                    customerId = customerId,
                    productId = productId,
                    quantity = quantity,
                    unitPrice = unitPrice,
                    totalAmount = quantity * unitPrice,
                    description = description,
                    dateTimestamp = System.currentTimeMillis(),
                    status = "pending"
                )
                quoteViewModel.addQuote(quote)
                showNewQuoteDialog = false
            }
        )
    }

    // Quote actions dialog
    if (showQuoteActions && selectedQuote != null) {
        QuoteActionsDialog(
            quote = selectedQuote!!,
            customer = allCustomers.find { it.id == selectedQuote!!.customerId },
            product = allProducts.find { it.id == selectedQuote!!.productId },
            companyInfo = companyInfo,
            context = context,
            onDismiss = { 
                showQuoteActions = false
                selectedQuote = null
            },
            onAccept = {
                val updatedQuote = selectedQuote!!.copy(status = "accepted")
                quoteViewModel.updateQuote(updatedQuote)
                showQuoteActions = false
                selectedQuote = null
            },
            onReject = {
                val updatedQuote = selectedQuote!!.copy(status = "rejected")
                quoteViewModel.updateQuote(updatedQuote)
                showQuoteActions = false
                selectedQuote = null
            },
            onConvertToInvoice = {
                // Créer la facture avec coroutines pour inclure les InvoiceItems
                CoroutineScope(Dispatchers.IO).launch {
                    val invoice = Invoice(
                        customerId = selectedQuote!!.customerId,
                        dateTimestamp = System.currentTimeMillis(),
                        totalAmount = selectedQuote!!.totalAmount,
                        isPaid = false
                    )
                    
                    // Insérer la facture et récupérer l'ID
                    val database = AppDatabase.getDatabase(context)
                    val invoiceDao = database.invoiceDao()
                    val invoiceItemDao = database.invoiceItemDao()
                    
                    val invoiceId = invoiceDao.insertInvoice(invoice)
                    
                    // Créer l'InvoiceItem correspondant au devis
                    val invoiceItem = InvoiceItem(
                        invoiceId = invoiceId,
                        productId = selectedQuote!!.productId,
                        quantity = selectedQuote!!.quantity,
                        unitPrice = selectedQuote!!.unitPrice,
                        totalPrice = selectedQuote!!.totalAmount,
                        description = if (selectedQuote!!.description.isNotEmpty()) selectedQuote!!.description else null
                    )
                    
                    invoiceItemDao.insertInvoiceItem(invoiceItem)
                    
                    withContext(Dispatchers.Main) {
                        val updatedQuote = selectedQuote!!.copy(status = "converted")
                        quoteViewModel.updateQuote(updatedQuote)
                        showQuoteActions = false
                        selectedQuote = null
                    }
                }
            },
            onDelete = {
                quoteViewModel.deleteQuote(selectedQuote!!)
                showQuoteActions = false
                selectedQuote = null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuoteActionCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = 16.dp)
            )
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuoteListItem(
    quote: Quote,
    customer: Customer?,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Devis #${quote.id}", fontWeight = FontWeight.Bold)
                    Text("Client: ${customer?.name ?: "Inconnu"}", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(quote.dateTimestamp)),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text("${String.format("%.2f", quote.totalAmount)}Dhs", fontWeight = FontWeight.Bold)
                    
                    val statusColor = when (quote.status) {
                        "pending" -> MaterialTheme.colorScheme.secondary
                        "accepted" -> MaterialTheme.colorScheme.primary
                        "rejected" -> MaterialTheme.colorScheme.error
                        "converted" -> MaterialTheme.colorScheme.tertiary
                        else -> MaterialTheme.colorScheme.outline
                    }
                    
                    val statusText = when (quote.status) {
                        "pending" -> "En attente"
                        "accepted" -> "Accepté"
                        "rejected" -> "Refusé"
                        "converted" -> "Converti"
                        else -> quote.status
                    }
                    
                    Card(
                        colors = CardDefaults.cardColors(containerColor = statusColor.copy(alpha = 0.1f))
                    ) {
                        Text(
                            statusText,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = statusColor
                        )
                    }
                }
            }
            
            if (quote.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(quote.description, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateQuoteDialog(
    customers: List<Customer>,
    products: List<Product>,
    onDismiss: () -> Unit,
    onConfirm: (Long, Long, Int, Double, String) -> Unit
) {
    var selectedCustomer by remember { mutableStateOf<Customer?>(null) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var quantity by remember { mutableStateOf("1") }
    var unitPrice by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var showCustomerDropdown by remember { mutableStateOf(false) }
    var showProductDropdown by remember { mutableStateOf(false) }
    var showCategoryDropdown by remember { mutableStateOf(false) }

    // Get unique categories from products
    val categories = products.flatMap { it.types }.distinct().sorted()
    
    // Filter products by category
    val filteredProducts = if (selectedCategory != null) {
        products.filter { it.types.contains(selectedCategory) }
    } else {
        products
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Créer un devis") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Debug info
                Text("Produits: ${products.size}, Catégories: ${categories.size}", 
                     style = MaterialTheme.typography.bodySmall)
                
                // Customer selection
                ExposedDropdownMenuBox(
                    expanded = showCustomerDropdown,
                    onExpandedChange = { showCustomerDropdown = it }
                ) {
                    OutlinedTextField(
                        value = selectedCustomer?.name ?: "",
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Client *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCustomerDropdown) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = showCustomerDropdown,
                        onDismissRequest = { showCustomerDropdown = false }
                    ) {
                        customers.forEach { customer ->
                            DropdownMenuItem(
                                text = { Text(customer.name) },
                                onClick = {
                                    selectedCustomer = customer
                                    showCustomerDropdown = false
                                }
                            )
                        }
                    }
                }

                // Category selection
                if (categories.isNotEmpty()) {
                    ExposedDropdownMenuBox(
                        expanded = showCategoryDropdown,
                        onExpandedChange = { showCategoryDropdown = it }
                    ) {
                        OutlinedTextField(
                            value = selectedCategory ?: "Toutes les catégories",
                            onValueChange = { },
                            readOnly = true,
                            label = { Text("Catégorie") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCategoryDropdown) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = showCategoryDropdown,
                            onDismissRequest = { showCategoryDropdown = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Toutes les catégories") },
                                onClick = {
                                    selectedCategory = null
                                    selectedProduct = null
                                    showCategoryDropdown = false
                                }
                            )
                            categories.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category) },
                                    onClick = {
                                        selectedCategory = category
                                        selectedProduct = null
                                        showCategoryDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Product selection
                ExposedDropdownMenuBox(
                    expanded = showProductDropdown,
                    onExpandedChange = { showProductDropdown = it }
                ) {
                    OutlinedTextField(
                        value = selectedProduct?.name ?: "",
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Produit * (${filteredProducts.size} disponibles)") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showProductDropdown) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = showProductDropdown,
                        onDismissRequest = { showProductDropdown = false }
                    ) {
                        filteredProducts.forEach { product ->
                            DropdownMenuItem(
                                text = { Text("${product.name} - ${String.format("%.2f", product.price)}Dhs") },
                                onClick = {
                                    selectedProduct = product
                                    unitPrice = product.price.toString()
                                    showProductDropdown = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Quantité *") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = unitPrice,
                    onValueChange = { unitPrice = it },
                    label = { Text("Prix unitaire *") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                // Preview
                if (selectedCustomer != null && selectedProduct != null && quantity.isNotEmpty() && unitPrice.isNotEmpty()) {
                    val total = (quantity.toIntOrNull() ?: 0) * (unitPrice.toDoubleOrNull() ?: 0.0)
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Aperçu du devis", fontWeight = FontWeight.Bold)
                            Text("Total: ${String.format("%.2f", total)}Dhs")
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (selectedCustomer != null && selectedProduct != null && 
                        quantity.isNotEmpty() && unitPrice.isNotEmpty()) {
                        onConfirm(
                            selectedCustomer!!.id,
                            selectedProduct!!.id,
                            quantity.toInt(),
                            unitPrice.toDouble(),
                            description
                        )
                    }
                }
            ) {
                Text("Créer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}

@Composable
fun QuoteActionsDialog(
    quote: Quote,
    customer: Customer?,
    product: Product?,
    companyInfo: CompanyInfo?,
    context: Context,
    onDismiss: () -> Unit,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onConvertToInvoice: () -> Unit,
    onDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Actions sur le devis #${quote.id}") },
        text = {
            Column {
                Text("Client: ${customer?.name ?: "Inconnu"}")
                Text("Montant: ${String.format("%.2f", quote.totalAmount)}Dhs")
                Text("Statut: ${quote.status}")
                if (quote.description.isNotEmpty()) {
                    Text("Description: ${quote.description}")
                }
            }
        },
        confirmButton = {
            Column {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Bouton PDF toujours disponible
                    TextButton(
                        onClick = {
                            launchQuotePrintPreview(context, quote, customer, product, companyInfo)
                        }
                    ) {
                        Text("📄 PDF")
                    }
                    
                    if (quote.status == "pending") {
                        TextButton(onClick = onAccept) {
                            Text("Accepter")
                        }
                        TextButton(onClick = onReject) {
                            Text("Refuser")
                        }
                    }
                    
                    if (quote.status == "accepted") {
                        TextButton(onClick = onConvertToInvoice) {
                            Text("→ Facture")
                        }
                    }
                }
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = onDelete) {
                        Text("Supprimer")
                    }
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Fermer")
            }
        }
    )
}

fun launchQuotePrintPreview(
    context: Context, 
    quote: Quote, 
    customer: Customer?, 
    product: Product?, 
    companyInfo: CompanyInfo?
) {
    val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
    printManager.print(
        "Devis_${quote.id}",
        QuotePrintAdapter(
            context = context,
            quote = quote, 
            customer = customer, 
            product = product,
            companyInfo = companyInfo
        ),
        PrintAttributes.Builder().build()
    )
}