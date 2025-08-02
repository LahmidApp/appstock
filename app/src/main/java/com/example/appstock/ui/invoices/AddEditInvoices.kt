package com.example.appstock.ui.invoices

import android.content.Context
import android.print.PrintAttributes
import android.print.PrintManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.appstock.data.AppDatabase
import com.example.appstock.data.InvoiceItem
import com.example.appstock.viewmodel.SaleViewModel
import com.example.appstock.viewmodel.CustomerViewModel
import com.example.appstock.viewmodel.ProductViewModel
import com.example.appstock.viewmodel.InvoiceViewModel
import com.example.appstock.data.Sale
import com.example.appstock.data.Customer
import com.example.appstock.data.Product
import com.example.appstock.data.Invoice
import com.example.appstock.ui.utils.getCompanyInfoViewModel
import com.example.appstock.data.CompanyInfo
import com.example.appstock.viewmodel.CompanyInfoViewModel
import com.example.appstock.utils.InvoicePrintAdapter
import com.example.appstock.ui.utils.getSaleViewModel
import com.example.appstock.ui.utils.getCustomerViewModel
import com.example.appstock.ui.utils.getProductViewModel
import com.example.appstock.ui.utils.getInvoiceViewModel
import com.example.appstock.ui.utils.getSaleHeaderViewModel
import com.example.appstock.data.*
import com.example.appstock.viewmodel.*
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditInvoicesScreen(
    saleViewModel: SaleViewModel = getSaleViewModel(),
    saleHeaderViewModel: SaleHeaderViewModel = getSaleHeaderViewModel(),
    customerViewModel: CustomerViewModel = getCustomerViewModel(),
    productViewModel: ProductViewModel = getProductViewModel(),
    invoiceViewModel: InvoiceViewModel = getInvoiceViewModel(),
    companyInfoViewModel: CompanyInfoViewModel = getCompanyInfoViewModel()
) {
    val context = LocalContext.current
    val allSales by saleViewModel.allSales.observeAsState(emptyList())
    val allSaleHeaders by saleHeaderViewModel.allSaleHeaders.observeAsState(emptyList())
    val allCustomers by customerViewModel.allCustomers.observeAsState(emptyList())
    val allProducts by productViewModel.allProducts.observeAsState(emptyList())
    val companyInfo by companyInfoViewModel.companyInfo.observeAsState()

    var selectedCustomer by remember { mutableStateOf<Customer?>(null) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var selectedSale by remember { mutableStateOf<Sale?>(null) }
    var selectedSaleHeader by remember { mutableStateOf<SaleHeader?>(null) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var showCategoryDropdown by remember { mutableStateOf(false) }
    var showCustomerDropdown by remember { mutableStateOf(false) }
    var showProductDropdown by remember { mutableStateOf(false) }
    var showSaleDropdown by remember { mutableStateOf(false) }
    var showSaleHeaderDropdown by remember { mutableStateOf(false) }
    var customerSearch by remember { mutableStateOf("") }
    var productSearch by remember { mutableStateOf("") }
    var saleSearch by remember { mutableStateOf("") }
    var saleHeaderSearch by remember { mutableStateOf("") }

    // Get unique categories from products
    val categories = allProducts.flatMap { it.types }.distinct().sorted()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Créer une facture",
            style = MaterialTheme.typography.headlineMedium
        )

        // Sélection de la catégorie
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("1. Sélectionner une catégorie (optionnel)", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                
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
        }

        // Sélection du client
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("2. Sélectionner un client", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                
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
                        allCustomers.forEach { customer ->
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
                
                selectedCustomer?.let { customer ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Client sélectionné: ${customer.name}")
                            Text("ID: ${customer.id}")
                            customer.email?.let { Text("Email: $it") }
                        }
                    }
                }
            }
        }

        // Sélection du produit
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("3. Sélectionner un produit", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                
                val filteredProducts = if (selectedCategory != null) {
                    allProducts.filter { it.types.contains(selectedCategory) }
                } else {
                    allProducts
                }
                
                ExposedDropdownMenuBox(
                    expanded = showProductDropdown,
                    onExpandedChange = { showProductDropdown = it }
                ) {
                    OutlinedTextField(
                        value = selectedProduct?.name ?: "",
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Produit *") },
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
                                    showProductDropdown = false
                                }
                            )
                        }
                    }
                }
                
                selectedProduct?.let { product ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Produit sélectionné: ${product.name}")
                            Text("ID: ${product.id}")
                            Text("Prix: ${product.price}Dhs")
                        }
                    }
                }
            }
        }

        // Liste des ventes disponibles (Legacy - mono-produit)
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("4a. Ventes mono-produit (Legacy)", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                
                val filteredSales = when {
                    selectedCustomer != null && selectedProduct != null -> 
                        allSales.filter { it.customerId == selectedCustomer!!.id && it.productId == selectedProduct!!.id }
                    selectedCustomer != null -> 
                        allSales.filter { it.customerId == selectedCustomer!!.id }
                    selectedProduct != null -> 
                        allSales.filter { it.productId == selectedProduct!!.id }
                    else -> allSales
                }

                if (filteredSales.isEmpty()) {
                    Text("Aucune vente trouvée avec ces critères")
                } else {
                    LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                        items(filteredSales) { sale ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                onClick = {
                                    selectedSale = sale
                                },
                                colors = if (selectedSale?.id == sale.id) {
                                    CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                                } else {
                                    CardDefaults.cardColors()
                                }
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("Vente ID: ${sale.id}")
                                    Text("Client ID: ${sale.customerId}")
                                    Text("Produit ID: ${sale.productId}")
                                    Text("Quantité: ${sale.quantity}")
                                    Text("Prix total: ${sale.totalPrice}Dhs")
                                    Text("Date: ${java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(java.util.Date(sale.saleDate))}")
                                }
                            }
                        }
                    }
                }
            }
        }

        // Liste des ventes multi-produits (Nouveau système)
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("4b. Ventes multi-produits (Recommandé)", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                
                val filteredSaleHeaders = when {
                    selectedCustomer != null -> 
                        allSaleHeaders.filter { it.customerId == selectedCustomer!!.id }
                    else -> allSaleHeaders
                }

                if (filteredSaleHeaders.isEmpty()) {
                    Text("Aucune vente multi-produit trouvée avec ces critères")
                } else {
                    LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                        items(filteredSaleHeaders) { saleHeader ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                onClick = {
                                    selectedSaleHeader = saleHeader
                                    selectedSale = null // Déselectionner la vente legacy
                                },
                                colors = if (selectedSaleHeader?.id == saleHeader.id) {
                                    CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                                } else {
                                    CardDefaults.cardColors()
                                }
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("Vente Multi-Produit ID: ${saleHeader.id}", fontWeight = FontWeight.Bold)
                                    Text("Client: ${allCustomers.find { it.id == saleHeader.customerId }?.name ?: "Client direct"}")
                                    Text("Montant total: ${String.format("%.2f", saleHeader.totalAmount)} Dhs")
                                    Text("Date: ${java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date(saleHeader.dateTimestamp))}")
                                    if (saleHeader.notes?.isNotEmpty() == true) {
                                        Text("Notes: ${saleHeader.notes}", style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Boutons d'action
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = {
                    when {
                        selectedSale != null -> {
                            // Créer la facture avec coroutines pour inclure les InvoiceItems
                            CoroutineScope(Dispatchers.IO).launch {
                                val invoice = Invoice(
                                    customerId = selectedSale!!.customerId,
                                    dateTimestamp = System.currentTimeMillis(),
                                    totalAmount = selectedSale!!.totalPrice,
                                    isPaid = false
                                )
                                
                                // Insérer la facture et récupérer l'ID
                                val database = AppDatabase.getDatabase(context)
                                val invoiceDao = database.invoiceDao()
                                val invoiceItemDao = database.invoiceItemDao()
                                
                                val invoiceId = invoiceDao.insertInvoice(invoice)
                                
                                // Créer l'InvoiceItem correspondant à la vente
                                val invoiceItem = InvoiceItem(
                                    invoiceId = invoiceId,
                                    productId = selectedSale!!.productId,
                                    quantity = selectedSale!!.quantity,
                                    unitPrice = selectedSale!!.unitPrice,
                                    totalPrice = selectedSale!!.totalPrice,
                                    description = null // Produit de l'inventaire
                                )
                                
                                invoiceItemDao.insertInvoiceItem(invoiceItem)
                                
                                withContext(Dispatchers.Main) {
                                    // Reset form
                                    selectedCustomer = null
                                    selectedProduct = null
                                    selectedSale = null
                                    selectedSaleHeader = null
                                    customerSearch = ""
                                    productSearch = ""
                                    saleSearch = ""
                                    saleHeaderSearch = ""
                                }
                            }
                        }
                        selectedSaleHeader != null -> {
                            // Créer la facture avec coroutines pour inclure les InvoiceItems
                            CoroutineScope(Dispatchers.IO).launch {
                                val invoice = Invoice(
                                    customerId = selectedSaleHeader!!.customerId,
                                    dateTimestamp = System.currentTimeMillis(),
                                    totalAmount = selectedSaleHeader!!.totalAmount,
                                    isPaid = false
                                )
                                
                                // Insérer la facture et récupérer l'ID
                                val database = AppDatabase.getDatabase(context)
                                val invoiceDao = database.invoiceDao()
                                val invoiceItemDao = database.invoiceItemDao()
                                val saleItemDao = database.saleItemDao()
                                
                                val invoiceId = invoiceDao.insertInvoice(invoice)
                                
                                // Récupérer les SaleItems pour cette vente
                                val saleItems = saleItemDao.getItemsForSaleSync(selectedSaleHeader!!.id)
                                
                                // Créer les InvoiceItems correspondants
                                saleItems.forEach { saleItem ->
                                    val invoiceItem = InvoiceItem(
                                        invoiceId = invoiceId,
                                        productId = saleItem.productId,
                                        quantity = saleItem.quantity,
                                        unitPrice = saleItem.unitPrice,
                                        totalPrice = saleItem.totalPrice,
                                        description = null // Produit de l'inventaire
                                    )
                                    invoiceItemDao.insertInvoiceItem(invoiceItem)
                                }
                                
                                withContext(Dispatchers.Main) {
                                    // Reset form
                                    selectedCustomer = null
                                    selectedProduct = null
                                    selectedSale = null
                                    selectedSaleHeader = null
                                    customerSearch = ""
                                    productSearch = ""
                                    saleSearch = ""
                                    saleHeaderSearch = ""
                                }
                            }
                        }
                    }
                },
                enabled = selectedSale != null || selectedSaleHeader != null,
                modifier = Modifier.weight(1f)
            ) {
                Text("Créer la facture")
            }

            Button(
                onClick = {
                    when {
                        selectedSale != null -> {
                            val invoice = Invoice(
                                customerId = selectedSale!!.customerId,
                                dateTimestamp = System.currentTimeMillis(),
                                totalAmount = selectedSale!!.totalPrice,
                                isPaid = false
                            )
                            // Pour le client, utiliser selectedCustomer ou créer un client fictif pour les ventes directes
                            val customerForPdf = selectedCustomer ?: Customer(
                                id = 0,
                                name = "Client direct",
                                email = "",
                                phoneNumber = "",
                                address = ""
                            )
                            launchPrintPreview(context, invoice, customerForPdf, selectedSale, companyInfo)
                        }
                        selectedSaleHeader != null -> {
                            val invoice = Invoice(
                                customerId = selectedSaleHeader!!.customerId,
                                dateTimestamp = System.currentTimeMillis(),
                                totalAmount = selectedSaleHeader!!.totalAmount,
                                isPaid = false
                            )
                            val customerForPdf = allCustomers.find { it.id == selectedSaleHeader!!.customerId } ?: Customer(
                                id = 0,
                                name = "Client direct",
                                email = "",
                                phoneNumber = "",
                                address = ""
                            )
                            launchPrintPreviewMultiProduct(context, invoice, customerForPdf, selectedSaleHeader!!, saleHeaderViewModel, allProducts, companyInfo)
                        }
                    }
                },
                enabled = selectedSale != null || selectedSaleHeader != null,
                modifier = Modifier.weight(1f)
            ) {
                Text("Prévisualiser PDF")
            }
        }
        
        Divider(modifier = Modifier.padding(vertical = 16.dp))
        
        // Section de facture libre
        FreeInvoiceSection(
            customers = allCustomers,
            products = allProducts,
            companyInfo = companyInfo,
            context = context,
            onCreateInvoice = { invoice ->
                invoiceViewModel.addInvoice(invoice)
            }
        )
    }
}

@Composable
fun DropdownMenuBox(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(modifier) {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = onDismissRequest,
            content = content
        )
    }
}

fun launchPrintPreview(context: Context, invoice: Invoice, customer: Customer?, sale: Sale?, companyInfo: CompanyInfo?) {
    val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
    
    // Créer des InvoiceItems et Products à partir de la vente legacy si disponible
    val invoiceItems = if (sale != null) {
        listOf(
            InvoiceItem(
                id = 0,
                invoiceId = invoice.id,
                productId = sale.productId,
                quantity = sale.quantity,
                unitPrice = sale.unitPrice,
                totalPrice = sale.totalPrice
            )
        )
    } else emptyList()
    
    // Récupérer les produits via l'application context si possible
    val products = try {
        // NOTE: Dans un contexte réel, vous devriez passer les produits ou utiliser un Repository
        // Pour l'instant, créer un produit fictif si la vente existe
        if (sale != null) {
            listOf(
                Product(
                    id = sale.productId,
                    name = "Produit #${sale.productId}",
                    price = sale.unitPrice,
                    stock = 1,
                    barcode = "",
                    qrCode = "",
                    description = null,
                    photoUri = null,
                    types = emptyList(),
                    costPrice = sale.unitPrice * 0.7, // Estimation du coût
                    minStockLevel = 1,
                    supplier = null
                )
            )
        } else emptyList()
    } catch (e: Exception) {
        emptyList<Product>()
    }
    
    printManager.print(
        "Facture_${invoice.id}",
        InvoicePrintAdapter(
            context = context,
            invoice = invoice, 
            customer = customer, 
            sale = sale,
            companyInfo = companyInfo,
            invoiceItems = invoiceItems,
            products = products
        ),
        PrintAttributes.Builder().build()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FreeInvoiceSection(
    customers: List<Customer>,
    products: List<Product>,
    companyInfo: CompanyInfo?,
    context: Context,
    onCreateInvoice: (Invoice) -> Unit
) {
    var selectedCustomer by remember { mutableStateOf<Customer?>(null) }
    var customerName by remember { mutableStateOf("") }
    var showCustomerDropdown by remember { mutableStateOf(false) }
    var invoiceItems by remember { mutableStateOf(listOf<FreeInvoiceItem>()) }
    var showAddItemDialog by remember { mutableStateOf(false) }

    Card {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Créer une facture libre", 
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                "Interface flexible avec suggestions automatiques",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            // Sélection du client avec suggestion
            ExposedDropdownMenuBox(
                expanded = showCustomerDropdown,
                onExpandedChange = { showCustomerDropdown = it }
            ) {
                OutlinedTextField(
                    value = customerName,
                    onValueChange = { 
                        customerName = it
                        selectedCustomer = null
                        showCustomerDropdown = it.isNotEmpty()
                    },
                    label = { Text("Client (optionnel - laissez vide pour client direct)") },
                    placeholder = { Text("Tapez le nom du client...") },
                    trailingIcon = { 
                        if (customerName.isNotEmpty()) {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCustomerDropdown)
                        }
                    },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                
                if (showCustomerDropdown && customerName.isNotEmpty()) {
                    val filteredCustomers = customers.filter { 
                        it.name.contains(customerName, ignoreCase = true) 
                    }
                    
                    ExposedDropdownMenu(
                        expanded = showCustomerDropdown,
                        onDismissRequest = { showCustomerDropdown = false }
                    ) {
                        filteredCustomers.forEach { customer ->
                            DropdownMenuItem(
                                text = { Text(customer.name) },
                                onClick = {
                                    selectedCustomer = customer
                                    customerName = customer.name
                                    showCustomerDropdown = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Liste des articles
            Text("Articles de la facture", style = MaterialTheme.typography.titleSmall)
            
            if (invoiceItems.isEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Aucun article ajouté",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                invoiceItems.forEach { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.description, fontWeight = FontWeight.Medium)
                                Text(
                                    "${item.quantity} x ${String.format("%.2f", item.unitPrice)}Dhs = ${String.format("%.2f", item.totalPrice)}Dhs",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            
                            IconButton(
                                onClick = {
                                    invoiceItems = invoiceItems.filter { it != item }
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
                val totalAmount = invoiceItems.sumOf { it.totalPrice }
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total général", fontWeight = FontWeight.Bold)
                        Text("${String.format("%.2f", totalAmount)}Dhs", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Boutons d'action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { showAddItemDialog = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Ajouter article")
                }
                
                Button(
                    onClick = {
                        if (invoiceItems.isNotEmpty()) {
                            val invoice = Invoice(
                                customerId = selectedCustomer?.id,
                                dateTimestamp = System.currentTimeMillis(),
                                totalAmount = invoiceItems.sumOf { it.totalPrice },
                                isPaid = false
                            )
                            
                            // Créer la facture et sauvegarder les articles
                            CoroutineScope(Dispatchers.IO).launch {
                                val database = AppDatabase.getDatabase(context)
                                val invoiceDao = database.invoiceDao()
                                val invoiceItemDao = database.invoiceItemDao()
                                
                                // Insérer la facture et récupérer l'ID
                                val invoiceId = invoiceDao.insertInvoice(invoice)
                                
                                // Créer et insérer les InvoiceItems
                                val dbInvoiceItems = invoiceItems.map { freeItem ->
                                    InvoiceItem(
                                        id = 0,
                                        invoiceId = invoiceId,
                                        productId = 0, // Pour les articles libres, pas de produit associé
                                        quantity = freeItem.quantity,
                                        unitPrice = freeItem.unitPrice,
                                        totalPrice = freeItem.totalPrice,
                                        description = freeItem.description // Ajoutons une description
                                    )
                                }
                                
                                dbInvoiceItems.forEach { item ->
                                    invoiceItemDao.insertInvoiceItem(item)
                                }
                                
                                withContext(Dispatchers.Main) {
                                    onCreateInvoice(invoice.copy(id = invoiceId))
                                    
                                    // Reset formulaire
                                    selectedCustomer = null
                                    customerName = ""
                                    invoiceItems = emptyList()
                                }
                            }
                        }
                    },
                    enabled = invoiceItems.isNotEmpty(),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Créer facture")
                }
            }
        }
    }

    // Dialog d'ajout d'article
    if (showAddItemDialog) {
        AddFreeInvoiceItemDialog(
            products = products,
            onDismiss = { showAddItemDialog = false },
            onConfirm = { item ->
                invoiceItems = invoiceItems + item
                showAddItemDialog = false
            }
        )
    }
}

data class FreeInvoiceItem(
    val description: String,
    val quantity: Int,
    val unitPrice: Double,
    val totalPrice: Double
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFreeInvoiceItemDialog(
    products: List<Product>,
    onDismiss: () -> Unit,
    onConfirm: (FreeInvoiceItem) -> Unit
) {
    var description by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("1") }
    var unitPrice by remember { mutableStateOf("") }
    var showProductSuggestions by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ajouter un article") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Description avec suggestions de produits
                ExposedDropdownMenuBox(
                    expanded = showProductSuggestions,
                    onExpandedChange = { showProductSuggestions = it }
                ) {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { 
                            description = it
                            showProductSuggestions = it.isNotEmpty()
                        },
                        label = { Text("Description de l'article") },
                        placeholder = { Text("Tapez pour voir les suggestions...") },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    
                    if (showProductSuggestions && description.isNotEmpty()) {
                        val filteredProducts = products.filter { 
                            it.name.contains(description, ignoreCase = true) 
                        }.take(5)
                        
                        if (filteredProducts.isNotEmpty()) {
                            ExposedDropdownMenu(
                                expanded = showProductSuggestions,
                                onDismissRequest = { showProductSuggestions = false }
                            ) {
                                filteredProducts.forEach { product ->
                                    DropdownMenuItem(
                                        text = { 
                                            Column {
                                                Text(product.name)
                                                Text(
                                                    "${String.format("%.2f", product.price)}Dhs",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        },
                                        onClick = {
                                            description = product.name
                                            unitPrice = product.price.toString()
                                            showProductSuggestions = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { quantity = it },
                        label = { Text("Quantité") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = unitPrice,
                        onValueChange = { unitPrice = it },
                        label = { Text("Prix unitaire") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Aperçu du total
                if (quantity.isNotEmpty() && unitPrice.isNotEmpty()) {
                    val qty = quantity.toIntOrNull() ?: 0
                    val price = unitPrice.toDoubleOrNull() ?: 0.0
                    val total = qty * price
                    
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Text(
                            "Total: ${String.format("%.2f", total)}Dhs",
                            modifier = Modifier.padding(12.dp),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (description.isNotEmpty() && quantity.isNotEmpty() && unitPrice.isNotEmpty()) {
                        val qty = quantity.toIntOrNull() ?: 0
                        val price = unitPrice.toDoubleOrNull() ?: 0.0
                        val item = FreeInvoiceItem(
                            description = description,
                            quantity = qty,
                            unitPrice = price,
                            totalPrice = qty * price
                        )
                        onConfirm(item)
                    }
                },
                enabled = description.isNotEmpty() && quantity.isNotEmpty() && unitPrice.isNotEmpty()
            ) {
                Text("Ajouter")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
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
    companyInfo: CompanyInfo?
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
