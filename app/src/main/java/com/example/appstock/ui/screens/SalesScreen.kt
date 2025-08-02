package com.example.appstock.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Preview
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appstock.ui.components.StatCard
import com.example.appstock.ui.utils.getSaleViewModel
import com.example.appstock.ui.utils.getSaleHeaderViewModel
import com.example.appstock.ui.utils.getCustomerViewModel
import com.example.appstock.ui.utils.getProductViewModel
import com.example.appstock.ui.utils.getCompanyInfoViewModel
import com.example.appstock.data.Sale
import com.example.appstock.data.SaleHeader
import com.example.appstock.data.SaleItem
import com.example.appstock.data.Customer
import com.example.appstock.data.Product
import com.example.appstock.data.Invoice
import com.example.appstock.data.InvoiceItem
import com.example.appstock.data.AppDatabase
import com.example.appstock.utils.InvoicePrintAdapter
import com.example.appstock.viewmodel.SaleHeaderViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.content.Context
import android.print.PrintAttributes
import android.print.PrintManager
import java.text.SimpleDateFormat
import java.util.*

/**
 * Data class pour représenter un article de vente avant enregistrement
 */
data class SaleItemData(
    val productId: Long,
    val productName: String,
    val quantity: Int,
    val unitPrice: Double,
    val totalPrice: Double
)

/**
 * Sales screen for managing sales transactions
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesScreen() {
    val saleViewModel = getSaleViewModel()
    val saleHeaderViewModel = getSaleHeaderViewModel()
    val customerViewModel = getCustomerViewModel()
    val productViewModel = getProductViewModel()
    val companyInfoViewModel = getCompanyInfoViewModel()

    val allSales by saleViewModel.allSales.observeAsState(emptyList())
    val allSaleHeaders by saleHeaderViewModel.allSaleHeaders.observeAsState(emptyList())
    val allCustomers by customerViewModel.allCustomers.observeAsState(emptyList())
    val allProducts by productViewModel.allProducts.observeAsState(emptyList())
    val companyInfo by companyInfoViewModel.companyInfo.observeAsState()
    val context = LocalContext.current
    
    var showAddSaleDialog by remember { mutableStateOf(false) }
    var showSalesHistory by remember { mutableStateOf(false) }

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
                text = "Ventes",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            FloatingActionButton(
                onClick = { showAddSaleDialog = true },
                modifier = Modifier.size(56.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Nouvelle vente")
            }
        }
        // Statistics cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCard(
                title = "Total Ventes",
                value = allSales.size.toString(),
                icon = Icons.Default.ShoppingCart,
                modifier = Modifier.weight(1f)
            )
            StatCard(
                title = "CA Aujourd'hui",
                value = "${String.format("%.2f", allSales.filter { 
                    val today = System.currentTimeMillis()
                    val oneDayMillis = 24 * 60 * 60 * 1000L
                    it.saleDate >= (today - (today % oneDayMillis))
                }.sumOf { it.totalPrice })}Dhs",
                icon = Icons.Default.Analytics,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { showAddSaleDialog = true },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Nouvelle vente")
            }
            
            Button(
                onClick = { showSalesHistory = !showSalesHistory },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.History, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Historique")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sales list or history
        if (showSalesHistory || allSaleHeaders.isNotEmpty()) {
            Text(
                text = if (showSalesHistory) "Historique des ventes" else "Ventes récentes",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(allSaleHeaders.sortedByDescending { it.saleDate }) { saleHeader ->
                    SaleHeaderListItem(
                        saleHeader = saleHeader,
                        customer = allCustomers.find { it.id == saleHeader.customerId },
                        saleHeaderViewModel = saleHeaderViewModel,
                        allProducts = allProducts,
                        allCustomers = allCustomers,
                        companyInfo = companyInfo,
                        context = context
                    )
                }
                
                if (allSaleHeaders.isEmpty()) {
                    item {
                        Card {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.ShoppingCart, contentDescription = null)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Aucune vente enregistrée")
                                Text("Créez votre première vente !", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }

    // Add sale dialog
    if (showAddSaleDialog) {
        CreateSaleDialog(
            customers = allCustomers,
            products = allProducts,
            onDismiss = { showAddSaleDialog = false },
            onConfirm = { customerId, saleItems ->
                val totalAmount = saleItems.sumOf { it.totalPrice }
                
                // Créer l'en-tête de vente
                val saleHeader = SaleHeader(
                    customerId = customerId,
                    dateTimestamp = System.currentTimeMillis(),
                    totalAmount = totalAmount
                )
                
                // Créer les éléments de vente
                val items = saleItems.map { saleItemData ->
                    SaleItem(
                        saleId = 0, // sera mis à jour lors de l'insertion
                        productId = saleItemData.productId,
                        quantity = saleItemData.quantity,
                        unitPrice = saleItemData.unitPrice,
                        totalPrice = saleItemData.totalPrice
                    )
                }
                
                // Insérer la vente avec ses éléments
                saleHeaderViewModel.insertSaleWithItems(saleHeader, items)
                
                // Aussi insérer dans l'ancienne table pour compatibilité
                saleItems.forEach { saleItemData ->
                    val sale = Sale(
                        productId = saleItemData.productId,
                        customerId = customerId,
                        quantity = saleItemData.quantity,
                        unitPrice = saleItemData.unitPrice,
                        totalPrice = saleItemData.totalPrice,
                        dateTimestamp = System.currentTimeMillis()
                    )
                    saleViewModel.insert(sale)
                }
                
                showAddSaleDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaleListItem(
    sale: Sale,
    customer: Customer?,
    product: Product?
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Vente #${sale.id}", fontWeight = FontWeight.Bold)
                    Text("Client: ${customer?.name ?: "Client direct"}", style = MaterialTheme.typography.bodyMedium)
                    Text("Produit: ${product?.name ?: "Produit supprimé"}", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(sale.saleDate)),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text("${String.format("%.2f", sale.totalPrice)}Dhs", fontWeight = FontWeight.Bold)
                    Text("${sale.quantity} x ${String.format("%.2f", sale.unitPrice)}Dhs", 
                         style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateSaleDialog(
    customers: List<Customer>,
    products: List<Product>,
    onDismiss: () -> Unit,
    onConfirm: (Long?, List<SaleItemData>) -> Unit
) {
    var selectedCustomer by remember { mutableStateOf<Customer?>(null) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var quantity by remember { mutableStateOf("1") }
    var unitPrice by remember { mutableStateOf("") }
    var customerSearch by remember { mutableStateOf("") }
    var showCustomerDropdown by remember { mutableStateOf(false) }
    var productSearch by remember { mutableStateOf("") }
    var showProductDropdown by remember { mutableStateOf(false) }
    var showCategoryDropdown by remember { mutableStateOf(false) }
    
    // Liste des articles sélectionnés
    var saleItems by remember { mutableStateOf(listOf<SaleItemData>()) }

    // Get unique categories from products
    val categories = products.flatMap { it.types }.distinct().sorted()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nouvelle vente") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Customer selection (optionnel) avec auto-complétion
                ExposedDropdownMenuBox(
                    expanded = showCustomerDropdown,
                    onExpandedChange = { showCustomerDropdown = it }
                ) {
                    OutlinedTextField(
                        value = customerSearch,
                        onValueChange = { 
                            customerSearch = it
                            selectedCustomer = null
                            showCustomerDropdown = it.isNotEmpty()
                        },
                        label = { Text("Client (optionnel - tapez pour rechercher)") },
                        placeholder = { Text("Tapez le nom du client...") },
                        trailingIcon = { 
                            if (customerSearch.isNotEmpty()) {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCustomerDropdown)
                            }
                        },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    
                    if (showCustomerDropdown && customerSearch.isNotEmpty()) {
                        val filteredCustomers = customers.filter { 
                            it.name.contains(customerSearch, ignoreCase = true) 
                        }
                        
                        ExposedDropdownMenu(
                            expanded = showCustomerDropdown,
                            onDismissRequest = { showCustomerDropdown = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Client direct") },
                                onClick = {
                                    selectedCustomer = null
                                    customerSearch = "Client direct"
                                    showCustomerDropdown = false
                                }
                            )
                            filteredCustomers.forEach { customer ->
                                DropdownMenuItem(
                                    text = { Text(customer.name) },
                                    onClick = {
                                        selectedCustomer = customer
                                        customerSearch = customer.name
                                        showCustomerDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Category selection (optionnel)
                if (categories.isNotEmpty()) {
                    ExposedDropdownMenuBox(
                        expanded = showCategoryDropdown,
                        onExpandedChange = { showCategoryDropdown = it }
                    ) {
                        OutlinedTextField(
                            value = selectedCategory ?: "Toutes les catégories",
                            onValueChange = { },
                            readOnly = true,
                            label = { Text("Catégorie (optionnel)") },
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
                                    showCategoryDropdown = false
                                }
                            )
                            categories.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category) },
                                    onClick = {
                                        selectedCategory = category
                                        showCategoryDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Product selection - filtered by category avec auto-complétion
                val filteredProducts = if (selectedCategory != null) {
                    products.filter { it.types.contains(selectedCategory) }
                } else {
                    products
                }

                ExposedDropdownMenuBox(
                    expanded = showProductDropdown,
                    onExpandedChange = { showProductDropdown = it }
                ) {
                    OutlinedTextField(
                        value = productSearch,
                        onValueChange = { 
                            productSearch = it
                            selectedProduct = null
                            showProductDropdown = it.isNotEmpty()
                        },
                        label = { Text("Ajouter un produit (tapez pour rechercher)") },
                        placeholder = { Text("Tapez le nom du produit...") },
                        trailingIcon = { 
                            if (productSearch.isNotEmpty()) {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = showProductDropdown)
                            }
                        },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    
                    if (showProductDropdown && productSearch.isNotEmpty()) {
                        val searchFilteredProducts = filteredProducts.filter { 
                            it.name.contains(productSearch, ignoreCase = true) 
                        }
                        
                        ExposedDropdownMenu(
                            expanded = showProductDropdown,
                            onDismissRequest = { showProductDropdown = false }
                        ) {
                            searchFilteredProducts.forEach { product ->
                                DropdownMenuItem(
                                    text = { Text("${product.name} - ${String.format("%.2f", product.price)}Dhs") },
                                    onClick = {
                                        selectedProduct = product
                                        productSearch = product.name
                                        unitPrice = product.price.toString()
                                        showProductDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { quantity = it },
                        label = { Text("Quantité") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    OutlinedTextField(
                        value = unitPrice,
                        onValueChange = { unitPrice = it },
                        label = { Text("Prix unitaire") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                }
                
                // Bouton ajouter à la liste
                Button(
                    onClick = {
                        if (selectedProduct != null && quantity.isNotEmpty() && unitPrice.isNotEmpty()) {
                            val qty = quantity.toIntOrNull() ?: 0
                            val price = unitPrice.toDoubleOrNull() ?: 0.0
                            val total = qty * price
                            
                            val newItem = SaleItemData(
                                productId = selectedProduct!!.id,
                                productName = selectedProduct!!.name,
                                quantity = qty,
                                unitPrice = price,
                                totalPrice = total
                            )
                            
                            // Vérifier si le produit n'est pas déjà dans la liste
                            if (!saleItems.any { it.productId == selectedProduct!!.id }) {
                                saleItems = saleItems + newItem
                                // Reset des champs
                                selectedProduct = null
                                quantity = "1"
                                unitPrice = ""
                            }
                        }
                    },
                    enabled = selectedProduct != null && quantity.isNotEmpty() && unitPrice.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ajouter à la liste")
                }

                // Liste des articles sélectionnés
                if (saleItems.isNotEmpty()) {
                    Text("Articles sélectionnés:", fontWeight = FontWeight.Bold)
                    
                    saleItems.forEach { item ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.productName, fontWeight = FontWeight.Medium)
                                    Text(
                                        "${item.quantity} x ${String.format("%.2f", item.unitPrice)}Dhs = ${String.format("%.2f", item.totalPrice)}Dhs",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                
                                IconButton(
                                    onClick = {
                                        saleItems = saleItems.filter { it.productId != item.productId }
                                    }
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Supprimer",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                    
                    // Total général
                    val totalGeneral = saleItems.sumOf { it.totalPrice }
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Total général", fontWeight = FontWeight.Bold)
                            Text("${String.format("%.2f", totalGeneral)}Dhs", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (saleItems.isNotEmpty()) {
                        onConfirm(selectedCustomer?.id, saleItems)
                        onDismiss()
                    }
                },
                enabled = saleItems.isNotEmpty()
            ) {
                Text("Enregistrer la vente")
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
fun SaleHeaderListItem(
    saleHeader: SaleHeader,
    customer: Customer?,
    saleHeaderViewModel: SaleHeaderViewModel,
    allProducts: List<Product>,
    allCustomers: List<Customer>,
    companyInfo: com.example.appstock.data.CompanyInfo?,
    context: Context
) {
    var expanded by remember { mutableStateOf(false) }
    var saleItems by remember { mutableStateOf<List<SaleItem>>(emptyList()) }
    
    LaunchedEffect(saleHeader.id) {
        val liveData = saleHeaderViewModel.getSaleItemsForSale(saleHeader.id)
        liveData.observeForever { items ->
            saleItems = items
        }
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = customer?.name ?: "Client direct",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Date: ${saleHeader.saleDate}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "${saleItems.size} produit(s)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${String.format("%.2f", saleHeader.totalAmount)}Dhs",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "Réduire" else "Développer"
                    )
                }
            }
            
            if (expanded && saleItems.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))
                
                saleItems.forEach { item ->
                    val product = allProducts.find { it.id == item.productId }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = product?.name ?: "Produit inconnu",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Prix unitaire: ${String.format("%.2f", item.unitPrice)}Dhs",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Qté: ${item.quantity}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "${String.format("%.2f", item.totalPrice)}Dhs",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                Divider()
                Spacer(modifier = Modifier.height(12.dp))
                
                // Boutons d'action
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Prévisualiser la facture
                    OutlinedButton(
                        onClick = { 
                            // Créer une facture temporaire pour la prévisualisation
                            val invoice = Invoice(
                                customerId = saleHeader.customerId,
                                dateTimestamp = System.currentTimeMillis(),
                                totalAmount = saleHeader.totalAmount,
                                isPaid = saleHeader.isPaid // Utiliser le statut de la vente
                            )
                            val customerForPdf = allCustomers.find { it.id == saleHeader.customerId } ?: Customer(
                                id = 0,
                                name = "Client direct",
                                email = "",
                                phoneNumber = "",
                                address = ""
                            )
                            launchPrintPreviewMultiProduct(context, invoice, customerForPdf, saleHeader, saleHeaderViewModel, allProducts, companyInfo)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            Icons.Default.Preview,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Prévisualiser", fontSize = 12.sp)
                    }
                    
                    // Générer la facture
                    Button(
                        onClick = { 
                            // Créer une vraie facture et la sauvegarder
                            val invoice = Invoice(
                                customerId = saleHeader.customerId,
                                dateTimestamp = System.currentTimeMillis(),
                                totalAmount = saleHeader.totalAmount,
                                isPaid = saleHeader.isPaid // Utiliser le statut de la vente
                            )
                            
                            // Utiliser une coroutine pour créer les InvoiceItems
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    val database = AppDatabase.getDatabase(context)
                                    val invoiceDao = database.invoiceDao()
                                    val invoiceItemDao = database.invoiceItemDao()
                                    val saleItemDao = database.saleItemDao()
                                    
                                    // Créer la facture
                                    val invoiceId = invoiceDao.insertInvoice(invoice)
                                    
                                    // Récupérer les items de la vente
                                    val saleItems = saleItemDao.getItemsForSaleSync(saleHeader.id)
                                    
                                    // Créer les InvoiceItems
                                    saleItems.forEach { saleItem ->
                                        val invoiceItem = InvoiceItem(
                                            invoiceId = invoiceId,
                                            productId = saleItem.productId,
                                            quantity = saleItem.quantity,
                                            unitPrice = saleItem.unitPrice,
                                            totalPrice = saleItem.totalPrice
                                        )
                                        invoiceItemDao.insertInvoiceItem(invoiceItem)
                                    }
                                    
                                    // Retourner au thread principal pour lancer l'impression
                                    withContext(Dispatchers.Main) {
                                        val customerForPdf = allCustomers.find { it.id == saleHeader.customerId } ?: Customer(
                                            id = 0,
                                            name = "Client direct",
                                            email = "",
                                            phoneNumber = "",
                                            address = ""
                                        )
                                        launchPrintPreviewMultiProduct(context, invoice.copy(id = invoiceId), customerForPdf, saleHeader, saleHeaderViewModel, allProducts, companyInfo)
                                    }
                                } catch (e: Exception) {
                                    // En cas d'erreur, juste prévisualiser
                                    withContext(Dispatchers.Main) {
                                        val customerForPdf = allCustomers.find { it.id == saleHeader.customerId } ?: Customer(
                                            id = 0,
                                            name = "Client direct",
                                            email = "",
                                            phoneNumber = "",
                                            address = ""
                                        )
                                        launchPrintPreviewMultiProduct(context, invoice, customerForPdf, saleHeader, saleHeaderViewModel, allProducts, companyInfo)
                                    }
                                }
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            Icons.Default.Description,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Facture", fontSize = 12.sp)
                    }
                    
                    // Marquer comme payée (si pas déjà payée)
                    if (!saleHeader.isPaid) {
                        Button(
                            onClick = { 
                                saleHeaderViewModel.updatePaymentStatus(saleHeader.id, true)
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary
                            )
                        ) {
                            Icon(
                                Icons.Default.Payment,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Payée", fontSize = 12.sp)
                        }
                    } else {
                        // Indicateur que la vente est payée
                        Surface(
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "Payée",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Lance la prévisualisation PDF pour les ventes multi-produits
 */
fun launchPrintPreviewMultiProduct(
    context: Context, 
    invoice: Invoice, 
    customer: Customer?, 
    saleHeader: SaleHeader,
    saleHeaderViewModel: SaleHeaderViewModel,
    allProducts: List<Product>,
    companyInfo: com.example.appstock.data.CompanyInfo?
) {
    val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
    
    // Récupérer les items de la vente via la base de données
    val database = AppDatabase.getDatabase(context)
    val saleItemDao = database.saleItemDao()
    
    // Utiliser une coroutine pour récupérer les données
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val saleItems = saleItemDao.getItemsForSaleSync(saleHeader.id)
            
            // Convertir SaleItems en InvoiceItems
            val invoiceItems = saleItems.map { saleItem ->
                InvoiceItem(
                    id = 0,
                    invoiceId = invoice.id,
                    productId = saleItem.productId,
                    quantity = saleItem.quantity,
                    unitPrice = saleItem.unitPrice,
                    totalPrice = saleItem.totalPrice
                )
            }
            
            // Retourner au thread principal pour lancer l'impression
            withContext(Dispatchers.Main) {
                val printDocumentAdapter = InvoicePrintAdapter(
                    context = context,
                    invoice = invoice,
                    customer = customer,
                    sale = null,
                    companyInfo = companyInfo,
                    invoiceItems = invoiceItems,
                    products = allProducts
                )
                
                printManager.print(
                    "Facture_Multi_${invoice.id}_${customer?.name ?: "ClientDirect"}",
                    printDocumentAdapter,
                    PrintAttributes.Builder()
                        .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                        .setResolution(PrintAttributes.Resolution("pdf", "pdf", 600, 600))
                        .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                        .build()
                )
            }
        } catch (e: Exception) {
            // En cas d'erreur, utiliser les données par défaut
            withContext(Dispatchers.Main) {
                val printDocumentAdapter = InvoicePrintAdapter(
                    context = context,
                    invoice = invoice,
                    customer = customer,
                    sale = null,
                    companyInfo = companyInfo,
                    invoiceItems = emptyList(),
                    products = allProducts
                )
                
                printManager.print(
                    "Facture_Multi_${invoice.id}_${customer?.name ?: "ClientDirect"}",
                    printDocumentAdapter,
                    PrintAttributes.Builder()
                        .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                        .setResolution(PrintAttributes.Resolution("pdf", "pdf", 600, 600))
                        .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                        .build()
                )
            }
        }
    }
}

