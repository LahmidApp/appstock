package com.example.appstock.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.appstock.data.*
import com.example.appstock.ui.utils.*

/**
 * Écran de ventes multi-produits simplifié pour corriger les erreurs de compilation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnhancedSalesScreen() {
    val saleViewModel = getSaleViewModel()
    val customerViewModel = getCustomerViewModel()
    val productViewModel = getProductViewModel()

    val allSales by saleViewModel.allSales.observeAsState(emptyList())
    val allCustomers by customerViewModel.allCustomers.observeAsState(emptyList())
    val allProducts by productViewModel.allProducts.observeAsState(emptyList())
    
    var showAddSaleDialog by remember { mutableStateOf(false) }
    var showSalesHistory by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header avec recherche
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Ventes Multi-Produits",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            IconButton(onClick = { showSalesHistory = !showSalesHistory }) {
                Icon(Icons.Default.History, contentDescription = "Historique")
            }
        }
        
        // Barre de recherche
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text("Rechercher une vente...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Bouton d'ajout de vente
        Button(
            onClick = { showAddSaleDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Nouvelle Vente Multi-Produits")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Liste des ventes filtrées
        val filteredSales = if (searchText.isEmpty()) {
            allSales
        } else {
            allSales.filter { sale ->
                val customer = allCustomers.find { it.id == sale.customerId }
                val product = allProducts.find { it.id == sale.productId }
                customer?.name?.contains(searchText, ignoreCase = true) == true ||
                product?.name?.contains(searchText, ignoreCase = true) == true ||
                sale.id.toString().contains(searchText)
            }
        }
        
        if (filteredSales.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredSales.sortedByDescending { it.saleDate }) { sale ->
                    SimpleSaleListItem(
                        sale = sale,
                        customer = allCustomers.find { it.id == sale.customerId },
                        product = allProducts.find { it.id == sale.productId }
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.ShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (searchText.isEmpty()) "Aucune vente enregistrée" else "Aucune vente trouvée",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
    
    // Dialog pour ajouter une vente
    if (showAddSaleDialog) {
        SimpleSaleDialog(
            customers = allCustomers,
            products = allProducts,
            onSaleCreated = { sale ->
                saleViewModel.insert(sale)
                showAddSaleDialog = false
            },
            onDismiss = { showAddSaleDialog = false }
        )
    }
}

@Composable
fun SimpleSaleListItem(
    sale: Sale,
    customer: Customer?,
    product: Product?
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = customer?.name ?: "Client direct",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = product?.name ?: "Produit #${sale.productId}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Quantité: ${sale.quantity}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                Text(
                    text = "${String.format("%.2f", sale.totalPrice)} Dhs",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleSaleDialog(
    customers: List<Customer>,
    products: List<Product>,
    onSaleCreated: (Sale) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedCustomer by remember { mutableStateOf<Customer?>(null) }
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var quantity by remember { mutableStateOf("") }
    var unitPrice by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nouvelle Vente") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Sélection client
                Text("Client (optionnel)")
                LazyColumn(
                    modifier = Modifier.heightIn(max = 120.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier.clickable { selectedCustomer = null },
                            colors = if (selectedCustomer == null) {
                                CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                            } else {
                                CardDefaults.cardColors()
                            }
                        ) {
                            Text(
                                text = "Client direct",
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                    items(customers) { customer ->
                        Card(
                            modifier = Modifier.clickable { selectedCustomer = customer },
                            colors = if (selectedCustomer?.id == customer.id) {
                                CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                            } else {
                                CardDefaults.cardColors()
                            }
                        ) {
                            Text(
                                text = customer.name,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
                
                // Sélection produit
                Text("Produit *")
                LazyColumn(
                    modifier = Modifier.heightIn(max = 120.dp)
                ) {
                    items(products) { product ->
                        Card(
                            modifier = Modifier.clickable { 
                                selectedProduct = product 
                                unitPrice = product.price.toString()
                            },
                            colors = if (selectedProduct?.id == product.id) {
                                CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                            } else {
                                CardDefaults.cardColors()
                            }
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = product.name,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${product.price} Dhs (Stock: ${product.stock})",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
                
                // Quantité
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Quantité *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Prix unitaire
                OutlinedTextField(
                    value = unitPrice,
                    onValueChange = { unitPrice = it },
                    label = { Text("Prix unitaire *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedProduct != null && quantity.isNotEmpty() && unitPrice.isNotEmpty()) {
                        val qty = quantity.toIntOrNull() ?: 0
                        val price = unitPrice.toDoubleOrNull() ?: 0.0
                        
                        val sale = Sale(
                            customerId = selectedCustomer?.id,
                            productId = selectedProduct!!.id,
                            quantity = qty,
                            unitPrice = price,
                            totalPrice = qty * price,
                            dateTimestamp = System.currentTimeMillis(),
                            saleDate = System.currentTimeMillis()
                        )
                        onSaleCreated(sale)
                    }
                },
                enabled = selectedProduct != null && quantity.isNotEmpty() && unitPrice.isNotEmpty()
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
