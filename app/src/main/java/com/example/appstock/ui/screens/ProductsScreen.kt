package com.example.appstock.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType // Import pour le type de clavier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.example.appstock.ui.products.AddEditProductActivity
import com.example.appstock.data.AppDatabase
import com.example.appstock.data.ProductRepository
import com.example.appstock.viewmodel.ProductViewModelFactory
import com.example.appstock.data.Product
import com.example.appstock.viewmodel.ProductViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

// import java.math.BigDecimal // Supprimé si BigDecimal n'est plus utilisé ici
/**
 * Products screen for managing inventory
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable

@Suppress("UnusedMaterial3ScaffoldPaddingParameter")
fun ProductsScreen() {
    val context = LocalContext.current
    val database = remember(context) { AppDatabase.getDatabase(context) }
    val repository = remember(database) { ProductRepository(database.productDao()) }
    val viewModelFactory = remember(repository) { ProductViewModelFactory(repository) }
    val productViewModel: ProductViewModel = viewModel(factory = viewModelFactory)

    val products by productViewModel.products.collectAsState()
    val isLoading by productViewModel.isLoading.collectAsState()
    val error by productViewModel.error.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        productViewModel.loadProducts()
    }

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
                text = "Produits",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            FloatingActionButton(
                onClick = { showAddDialog = true },
                modifier = Modifier.size(56.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Ajouter un produit")
            }
        }

        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                if (it.isNotEmpty()) {
                    productViewModel.searchProducts(it)
                } else {
                    productViewModel.loadProducts()
                }
            },
            label = { Text("Rechercher un produit") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Error message
        error?.let { errorMessage ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        // Loading indicator
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Products list
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(products) { product ->
                    ProductCard(
                        product = product,
                        onEdit = { 
                            val intent = Intent(context, AddEditProductActivity::class.java)
                            intent.putExtra(AddEditProductActivity.EXTRA_PRODUCT_ID, product.id)
                            context.startActivity(intent)
                        },
                        onDelete = { productViewModel.deleteProduct(product.id) }
                    )
                }
            }
        }
    }

    // Add product dialog
    if (showAddDialog) {
        AddProductDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, description, price, costPrice, quantity, category, supplier, minStock ->
                productViewModel.addProduct(
                    name,
                    description,
                    price,
                    costPrice,
                    quantity,
                    category,
                    supplier,
                    minStock
                )
                showAddDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductCard(
    product: Product, // Product.price et Product.costPrice sont maintenant des Double
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )

                    product.description?.let { desc ->
                        Text(
                            text = desc,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Row(
                        modifier = Modifier.padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            // L'affichage du prix est déjà correct si product.price est Double
                            text = "Prix: ${product.price}Dhs", // Vous voudrez peut-être formater ce Double
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Stock: ${product.stock}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (product.minStockLevel != null && product.stock <= product.minStockLevel) { // Assurez-vous que minStockLevel n'est pas null pour la comparaison
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }

                    if (product.types.isNotEmpty()) {
                        Text(
                            text = "Types: ${product.types.joinToString(", ")}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Filled.Edit, contentDescription = "Modifier")
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = "Supprimer",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddProductDialog(
    onDismiss: () -> Unit,
    // CORRIGÉ: La signature attend maintenant des Double pour le prix et le coût.
    onConfirm: (String, String?, Double, Double, Int, List<String>, String?, Int) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priceStr by remember { mutableStateOf("") } // Garder en String pour l'input
    var costPriceStr by remember { mutableStateOf("") } // Garder en String pour l'input
    var quantityStr by remember { mutableStateOf("") } // Garder en String pour l'input
    var typeInput by remember { mutableStateOf("") }
    var typesList by remember { mutableStateOf(listOf<String>()) }
    var supplier by remember { mutableStateOf("") }
    var minStockStr by remember { mutableStateOf("") } // Garder en String pour l'input

    // États pour gérer les erreurs de validation des champs numériques
    var priceError by remember { mutableStateOf<String?>(null) }
    var costPriceError by remember { mutableStateOf<String?>(null) }
    var quantityError by remember { mutableStateOf<String?>(null) }
    var minStockError by remember { mutableStateOf<String?>(null) }


    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ajouter un produit") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nom du produit *") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = name.isBlank() // Simple validation pour nom vide
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = priceStr,
                        onValueChange = {
                            priceStr = it
                            priceError = null // Réinitialiser l'erreur lors de la saisie
                        },
                        label = { Text("Prix de vente *") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), // CORRIGÉ
                        isError = priceError != null || priceStr.isBlank(),
                        supportingText = { if (priceError != null) Text(priceError!!) }
                    )

                    OutlinedTextField(
                        value = costPriceStr,
                        onValueChange = {
                            costPriceStr = it
                            costPriceError = null // Réinitialiser l'erreur lors de la saisie
                        },
                        label = { Text("Prix d'achat *") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), // CORRIGÉ
                        isError = costPriceError != null || costPriceStr.isBlank(),
                        supportingText = { if (costPriceError != null) Text(costPriceError!!) }
                    )

                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = quantityStr,
                        onValueChange = {
                            quantityStr = it
                            quantityError = null // Réinitialiser l'erreur lors de la saisie
                        },
                        label = { Text("Quantité *") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = quantityError != null || quantityStr.isBlank(),
                        supportingText = { if (quantityError != null) Text(quantityError!!) }
                    )

                    OutlinedTextField(
                        value = minStockStr,
                        onValueChange = {
                            minStockStr = it
                            minStockError = null // Réinitialiser l'erreur lors de la saisie
                        },
                        label = { Text("Stock minimum") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = minStockError != null,
                        supportingText = { if (minStockError != null) Text(minStockError!!) }
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = typeInput,
                        onValueChange = { typeInput = it },
                        label = { Text("Type/Catégorie") },
                        modifier = Modifier.weight(1f)
                    )
                    Button(onClick = {
                        if (typeInput.isNotBlank() && !typesList.contains(typeInput)) {
                            typesList = typesList + typeInput
                            typeInput = ""
                        }
                    }, modifier = Modifier.padding(start = 8.dp)) {
                        Text("+")
                    }
                }
                if (typesList.isNotEmpty()) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        typesList.forEach { type ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = MaterialTheme.shapes.small
                                ) {
                                    Text(type, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                }
                                IconButton(onClick = { typesList = typesList - type }) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Supprimer type")
                                }
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = supplier,
                    onValueChange = { supplier = it },
                    label = { Text("Fournisseur") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    var isValid = true
                    val currentPrice = priceStr.toDoubleOrNull()
                    val currentCostPrice = costPriceStr.toDoubleOrNull()
                    val currentQuantity = quantityStr.toIntOrNull()
                    val currentMinStock = minStockStr.toIntOrNull() ?: 0 // Défaut à 0 si vide ou invalide

                    if (name.isBlank()) isValid = false
                    if (priceStr.isBlank() || currentPrice == null) {
                        priceError = "Prix invalide"
                        isValid = false
                    }
                    if (costPriceStr.isBlank() || currentCostPrice == null) {
                        costPriceError = "Coût invalide"
                        isValid = false
                    }
                    if (quantityStr.isBlank() || currentQuantity == null || currentQuantity < 0) {
                        quantityError = "Quantité invalide"
                        isValid = false
                    }
                    if (minStockStr.isNotBlank() && (currentMinStock < 0 || minStockStr.toIntOrNull() == null) ) { // Valider minStock seulement s'il n'est pas vide
                        minStockError = "Stock min. invalide"
                        isValid = false
                    }


                    if (isValid && currentPrice != null && currentCostPrice != null && currentQuantity != null) {
                        onConfirm(
                            name,
                            description.takeIf { it.isNotEmpty() },
                            currentPrice, // Converti en Double
                            currentCostPrice, // Converti en Double
                            currentQuantity,
                            typesList,
                            supplier.takeIf { it.isNotEmpty() },
                            currentMinStock
                        )
                    }
                }
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
