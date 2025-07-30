package com.example.appstock.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.lifecycle.ViewModelProvider
import com.example.appstock.data.LibraryDatabase
import com.example.appstock.data.ProductRepository
import com.example.appstock.data.SaleRepository
import com.example.appstock.viewmodel.SaleViewModel
import com.example.appstock.viewmodel.SaleViewModelFactory
import com.example.appstock.viewmodel.ProductViewModel
import com.example.appstock.viewmodel.ProductViewModelFactory

/**
 * Sales screen for managing sales transactions
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesScreen() {
    val context = androidx.compose.ui.platform.LocalContext.current
    // Product ViewModel pour la liste des produits
    val productViewModel: ProductViewModel = remember {
        val db = LibraryDatabase.getDatabase(context)
        val repo = ProductRepository(db.productDao())
        ViewModelProvider(
            context as androidx.lifecycle.ViewModelStoreOwner,
            ProductViewModelFactory(repo)
        )[ProductViewModel::class.java]
    }
    val products by productViewModel.products.collectAsState()

    // Sale ViewModel pour l'insertion
    val saleViewModel: SaleViewModel = remember {
        val db = LibraryDatabase.getDatabase(context)
        val repo = SaleRepository(db.saleDao())
        ViewModelProvider(
            context as androidx.lifecycle.ViewModelStoreOwner,
            SaleViewModelFactory(repo)
        )[SaleViewModel::class.java]
    }

    var showNewSaleDialog by remember { mutableStateOf(false) }

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
                onClick = { showNewSaleDialog = true },
                modifier = Modifier.size(56.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Nouvelle vente")
            }
        }

        // Quick actions
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                QuickSaleCard(
                    title = "Vente rapide",
                    description = "Scanner un produit et enregistrer la vente",
                    icon = Icons.Default.QrCodeScanner,
                    onClick = { showNewSaleDialog = true }
                )
            }

            item {
                QuickSaleCard(
                    title = "Vente avec panier",
                    description = "Ajouter plusieurs produits au panier",
                    icon = Icons.Filled.ShoppingCart,
                    onClick = { showNewSaleDialog = true }
                )
            }

            item {
                QuickSaleCard(
                    title = "Historique des ventes",
                    description = "Voir toutes les ventes effectuées",
                    icon = Icons.Default.History,
                    onClick = { /* TODO: Historique des ventes */ }
                )
            }

            item {
                QuickSaleCard(
                    title = "Rapports de ventes",
                    description = "Analyser les performances de vente",
                    icon = Icons.Default.Analytics,
                    onClick = { /* TODO: Rapports de ventes */ }
                )
            }
        }
    }

    if (showNewSaleDialog) {
        AddSaleDialog(
            products = products,
            onDismiss = { showNewSaleDialog = false },
            onConfirm = { productId, quantity, unitPrice ->
                val product = products.find { it.id == productId }
                if (product != null) {
                    val sale = com.example.appstock.data.Sale(
                        productId = productId,
                        quantity = quantity,
                        unitPrice = unitPrice,
                        totalPrice = unitPrice * quantity,
                        customerId = null,
                        dateTimestamp = System.currentTimeMillis()
                    )
                    saleViewModel.insert(sale)
                }
                showNewSaleDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSaleDialog(
    products: List<com.example.appstock.data.Product>,
    onDismiss: () -> Unit,
    onConfirm: (productId: Long, quantity: Int, unitPrice: Double) -> Unit
) {
    var selectedProductId by remember { mutableStateOf<Long?>(products.firstOrNull()?.id) }
    var quantityStr by remember { mutableStateOf("") }
    var unitPriceStr by remember { mutableStateOf("") }
    var quantityError by remember { mutableStateOf<String?>(null) }
    var priceError by remember { mutableStateOf<String?>(null) }

    val selectedProduct = products.find { it.id == selectedProductId }

    LaunchedEffect(selectedProductId) {
        selectedProduct?.let {
            unitPriceStr = it.price.toString()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nouvelle vente") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Sélection du produit
                Text("Produit")
                DropdownMenuBox(
                    products = products,
                    selectedProductId = selectedProductId,
                    onProductSelected = { id ->
                        selectedProductId = id
                    }
                )

                OutlinedTextField(
                    value = quantityStr,
                    onValueChange = {
                        quantityStr = it
                        quantityError = null
                    },
                    label = { Text("Quantité") },
                    keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                    isError = quantityError != null,
                    supportingText = { if (quantityError != null) Text(quantityError!!) }
                )

                OutlinedTextField(
                    value = unitPriceStr,
                    onValueChange = {
                        unitPriceStr = it
                        priceError = null
                    },
                    label = { Text("Prix unitaire") },
                    keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal),
                    isError = priceError != null,
                    supportingText = { if (priceError != null) Text(priceError!!) }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val quantity = quantityStr.toIntOrNull()
                    val unitPrice = unitPriceStr.toDoubleOrNull()
                    var valid = true
                    if (quantity == null || quantity <= 0) {
                        quantityError = "Quantité invalide"
                        valid = false
                    }
                    if (unitPrice == null || unitPrice <= 0.0) {
                        priceError = "Prix invalide"
                        valid = false
                    }
                    if (selectedProductId == null) valid = false
                    if (valid && selectedProductId != null && quantity != null && unitPrice != null) {
                        onConfirm(selectedProductId!!, quantity, unitPrice)
                    }
                }
            ) { Text("Ajouter") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        }
    )
}

@Composable
fun DropdownMenuBox(
    products: List<com.example.appstock.data.Product>,
    selectedProductId: Long?,
    onProductSelected: (Long) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedProduct = products.find { it.id == selectedProductId }
    OutlinedTextField(
        value = selectedProduct?.name ?: "",
        onValueChange = {},
        label = { Text("Sélectionner un produit") },
        readOnly = true,
        modifier = Modifier.fillMaxWidth().clickable { expanded = true }
    )
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        products.forEach { product ->
            DropdownMenuItem(
                text = { Text(product.name) },
                onClick = {
                    onProductSelected(product.id)
                    expanded = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickSaleCard(
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
                modifier = Modifier
                    .size(48.dp)
                    .padding(end = 16.dp)
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
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

