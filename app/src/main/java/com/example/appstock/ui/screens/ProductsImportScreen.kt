package com.example.appstock.ui.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.appstock.data.Product
import com.example.appstock.ui.utils.getProductViewModel
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Écran d'import de produits via fichier CSV
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsImportScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val productViewModel = getProductViewModel()
    
    var isImporting by remember { mutableStateOf(false) }
    var importResults by remember { mutableStateOf<ImportResults?>(null) }
    var previewProducts by remember { mutableStateOf<List<ProductImportRow>>(emptyList()) }
    var showPreview by remember { mutableStateOf(false) }
    
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                isImporting = true
                try {
                    val results = processCSVFile(context, uri)
                    previewProducts = results.validProducts
                    importResults = ImportResults(
                        totalRows = results.totalRows,
                        validProducts = results.validProducts,
                        errors = results.errors
                    )
                    showPreview = true
                } catch (e: Exception) {
                    importResults = ImportResults(
                        totalRows = 0,
                        validProducts = emptyList(),
                        errors = listOf("Erreur lors de la lecture du fichier: ${e.message}")
                    )
                } finally {
                    isImporting = false
                }
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Text(
            text = "Import de Produits (CSV)",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Instructions
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Format CSV attendu",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Votre fichier CSV doit contenir les colonnes suivantes (dans cet ordre) :",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                val csvFormat = """
                    1. name (requis) - Nom du produit
                    2. description (optionnel) - Description du produit
                    3. price (requis) - Prix en euros (ex: 19.99)
                    4. stock (optionnel, défaut: 0) - Quantité en stock
                    5. types (optionnel) - Catégories séparées par ; (ex: "électronique;accessoire")
                    6. supplier (optionnel) - Nom du fournisseur
                    7. barcode (optionnel) - Code-barres du produit
                """.trimIndent()
                
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = csvFormat,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Exemple de ligne CSV :",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = "Smartphone XYZ,Téléphone Android 128Go,299.99,15,\"électronique;téléphonie\",TechCorp,1234567890123",
                        modifier = Modifier.padding(8.dp),
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Les valeurs vides seront remplacées par des valeurs par défaut. Seuls le nom et le prix sont obligatoires.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Bouton de sélection de fichier
        Button(
            onClick = { filePickerLauncher.launch("text/*") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isImporting
        ) {
            if (isImporting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Analyse en cours...")
            } else {
                Icon(Icons.Default.Upload, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sélectionner un fichier CSV")
            }
        }
        
        // Résultats de l'import
        importResults?.let { results ->
            Spacer(modifier = Modifier.height(16.dp))
            
            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Résultats de l'analyse",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text("Total lignes analysées: ${results.totalRows}")
                    Text("Produits valides: ${results.validProducts.size}")
                    Text("Erreurs: ${results.errors.size}")
                    
                    if (results.errors.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Erreurs détectées:",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                        results.errors.forEach { error ->
                            Text(
                                text = "• $error",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    
                    if (results.validProducts.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = {
                                scope.launch {
                                    results.validProducts.forEach { productRow ->
                                        productViewModel.addProduct(
                                            name = productRow.name,
                                            description = productRow.description,
                                            price = productRow.price,
                                            costPrice = productRow.price * 0.7,
                                            quantity = productRow.stock,
                                            types = productRow.types,
                                            supplier = productRow.supplier,
                                            minStockLevel = 5
                                        )
                                    }
                                    
                                    // Reset après import
                                    importResults = null
                                    previewProducts = emptyList()
                                    showPreview = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Importer ${results.validProducts.size} produit(s)")
                        }
                    }
                }
            }
        }
        
        // Prévisualisation des produits
        if (showPreview && previewProducts.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Prévisualisation des produits",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 300.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(previewProducts.take(10)) { product ->
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = MaterialTheme.shapes.small
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = product.name,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (product.description.isNotEmpty()) {
                                        Text(
                                            text = product.description,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                    Row {
                                        Text("Prix: ${product.price}Dhs")
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Text("Stock: ${product.stock}")
                                    }
                                    if (product.types.isNotEmpty()) {
                                        Text(
                                            text = "Catégories: ${product.types.joinToString(", ")}",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                        }
                        
                        if (previewProducts.size > 10) {
                            item {
                                Text(
                                    text = "... et ${previewProducts.size - 10} autre(s) produit(s)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

data class ProductImportRow(
    val name: String,
    val description: String = "",
    val price: Double,
    val stock: Int = 0,
    val types: List<String> = emptyList(),
    val supplier: String? = null,
    val barcode: String? = null
)

data class ImportResults(
    val totalRows: Int,
    val validProducts: List<ProductImportRow>,
    val errors: List<String>
)

data class CSVParseResult(
    val totalRows: Int,
    val validProducts: List<ProductImportRow>,
    val errors: List<String>
)

private suspend fun processCSVFile(context: Context, uri: Uri): CSVParseResult {
    val validProducts = mutableListOf<ProductImportRow>()
    val errors = mutableListOf<String>()
    var totalRows = 0
    
    try {
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                var line: String?
                var lineNumber = 0
                
                while (reader.readLine().also { line = it } != null) {
                    lineNumber++
                    totalRows++
                    
                    if (line.isNullOrBlank()) continue
                    
                    try {
                        val columns = parseCSVLine(line!!)
                        
                        if (columns.isEmpty()) {
                            errors.add("Ligne $lineNumber: Ligne vide")
                            continue
                        }
                        
                        // Validation des colonnes minimales
                        if (columns.size < 3) {
                            errors.add("Ligne $lineNumber: Au moins 3 colonnes requises (nom, description, prix)")
                            continue
                        }
                        
                        val name = columns.getOrNull(0)?.trim()
                        if (name.isNullOrBlank()) {
                            errors.add("Ligne $lineNumber: Nom du produit requis")
                            continue
                        }
                        
                        val priceStr = columns.getOrNull(2)?.trim()
                        val price = priceStr?.toDoubleOrNull()
                        if (price == null || price < 0) {
                            errors.add("Ligne $lineNumber: Prix invalide '$priceStr'")
                            continue
                        }
                        
                        // Colonnes optionnelles avec valeurs par défaut
                        val description = columns.getOrNull(1)?.trim() ?: ""
                        val stock = columns.getOrNull(3)?.trim()?.toIntOrNull() ?: 0
                        val typesStr = columns.getOrNull(4)?.trim() ?: ""
                        val types = if (typesStr.isNotEmpty()) {
                            typesStr.split(";").map { it.trim() }.filter { it.isNotEmpty() }
                        } else emptyList()
                        val supplier = columns.getOrNull(5)?.trim()?.takeIf { it.isNotEmpty() }
                        val barcode = columns.getOrNull(6)?.trim()?.takeIf { it.isNotEmpty() }
                        
                        validProducts.add(
                            ProductImportRow(
                                name = name,
                                description = description,
                                price = price,
                                stock = maxOf(0, stock), // Assurer que le stock n'est pas négatif
                                types = types,
                                supplier = supplier,
                                barcode = barcode
                            )
                        )
                        
                    } catch (e: Exception) {
                        errors.add("Ligne $lineNumber: Erreur de traitement - ${e.message}")
                    }
                }
            }
        }
    } catch (e: Exception) {
        errors.add("Erreur de lecture du fichier: ${e.message}")
    }
    
    return CSVParseResult(
        totalRows = totalRows,
        validProducts = validProducts,
        errors = errors
    )
}

private fun parseCSVLine(line: String): List<String> {
    val result = mutableListOf<String>()
    var current = StringBuilder()
    var inQuotes = false
    var i = 0
    
    while (i < line.length) {
        val char = line[i]
        
        when {
            char == '"' && !inQuotes -> {
                inQuotes = true
            }
            char == '"' && inQuotes -> {
                if (i + 1 < line.length && line[i + 1] == '"') {
                    // Double quote escaped
                    current.append('"')
                    i++ // Skip next quote
                } else {
                    inQuotes = false
                }
            }
            char == ',' && !inQuotes -> {
                result.add(current.toString())
                current = StringBuilder()
            }
            else -> {
                current.append(char)
            }
        }
        i++
    }
    
    result.add(current.toString())
    return result
}
