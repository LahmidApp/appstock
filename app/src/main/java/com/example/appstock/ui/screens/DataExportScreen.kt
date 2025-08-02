package com.example.appstock.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.*
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
import com.example.appstock.ui.utils.*
import com.example.appstock.data.Product
import com.example.appstock.data.Customer
import com.example.appstock.data.Quote
import kotlinx.coroutines.launch
import androidx.compose.runtime.livedata.observeAsState

/**
 * Écran d'export de données en CSV
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataExportScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    val productViewModel = getProductViewModel()
    val customerViewModel = getCustomerViewModel()
    val quoteViewModel = getQuoteViewModel()
    
    val products: List<Product> by productViewModel.allProducts.observeAsState(initial = emptyList())
    val customers: List<Customer> by customerViewModel.allCustomers.observeAsState(initial = emptyList())
    val quotes: List<Quote> by quoteViewModel.allQuotes.observeAsState(initial = emptyList())
    
    var isExporting by remember { mutableStateOf(false) }
    var exportMessage by remember { mutableStateOf<String?>(null) }
    var exportType by remember { mutableStateOf<String?>(null) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Text(
            text = "Export de Données",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Statistiques
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Données disponibles",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatsCard("Produits", products.size, Icons.Default.Inventory)
                    StatsCard("Clients", customers.size, Icons.Default.People)
                    StatsCard("Devis", quotes.size, Icons.Default.Description)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Options d'export
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Exporter vos données",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Export Produits
                ExportButton(
                    title = "Exporter les Produits",
                    description = "${products.size} produit(s) disponible(s)",
                    icon = Icons.Default.Inventory,
                    enabled = products.isNotEmpty() && !isExporting,
                    onClick = {
                        scope.launch {
                            isExporting = true
                            exportType = "Produits"
                            try {
                                val uri = CSVExportUtility.exportProducts(context, products)
                                if (uri != null) {
                                    exportMessage = "Export réussi ! ${products.size} produit(s) exporté(s)"
                                    CSVExportUtility.shareCSVFile(context, uri, "produits")
                                } else {
                                    exportMessage = "Erreur d'export des produits"
                                }
                            } catch (error: Exception) {
                                exportMessage = "Erreur d'export: ${error.message}"
                            } finally {
                                isExporting = false
                                exportType = null
                            }
                        }
                    }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Export Clients
                ExportButton(
                    title = "Exporter les Clients",
                    description = "${customers.size} client(s) disponible(s)",
                    icon = Icons.Default.People,
                    enabled = customers.isNotEmpty() && !isExporting,
                    onClick = {
                        scope.launch {
                            isExporting = true
                            exportType = "Clients"
                            try {
                                val uri = CSVExportUtility.exportCustomers(context, customers)
                                if (uri != null) {
                                    exportMessage = "Export réussi ! ${customers.size} client(s) exporté(s)"
                                    CSVExportUtility.shareCSVFile(context, uri, "clients")
                                } else {
                                    exportMessage = "Erreur d'export des clients"
                                }
                            } catch (error: Exception) {
                                exportMessage = "Erreur d'export: ${error.message}"
                            } finally {
                                isExporting = false
                                exportType = null
                            }
                        }
                    }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Export Devis
                ExportButton(
                    title = "Exporter les Devis",
                    description = "${quotes.size} devis disponible(s)",
                    icon = Icons.Default.Description,
                    enabled = quotes.isNotEmpty() && !isExporting,
                    onClick = {
                        scope.launch {
                            isExporting = true
                            exportType = "Devis"
                            try {
                                val uri = CSVExportUtility.exportQuotes(context, quotes)
                                if (uri != null) {
                                    exportMessage = "Export réussi ! ${quotes.size} devis exporté(s)"
                                    CSVExportUtility.shareCSVFile(context, uri, "devis")
                                } else {
                                    exportMessage = "Erreur d'export des devis"
                                }
                            } catch (error: Exception) {
                                exportMessage = "Erreur d'export: ${error.message}"
                            } finally {
                                isExporting = false
                                exportType = null
                            }
                        }
                    }
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Export Ventes (à implémenter quand les données seront disponibles)
                ExportButton(
                    title = "Exporter les Ventes",
                    description = "Ventes avec détails des produits",
                    icon = Icons.Default.ShoppingCart,
                    enabled = false, // À activer quand les ventes seront disponibles
                    onClick = {
                        // TODO: Implémenter quand les ventes seront disponibles
                    }
                )
            }
        }
        
        // Statut d'export
        if (isExporting) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Export en cours: $exportType...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        
        // Message de résultat
        exportMessage?.let { message ->
            Spacer(modifier = Modifier.height(16.dp))
            
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (message.startsWith("Erreur")) {
                        MaterialTheme.colorScheme.errorContainer
                    } else {
                        MaterialTheme.colorScheme.primaryContainer
                    }
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (message.startsWith("Erreur")) {
                            Icons.Default.Error
                        } else {
                            Icons.Default.CheckCircle
                        },
                        contentDescription = null,
                        tint = if (message.startsWith("Erreur")) {
                            MaterialTheme.colorScheme.onErrorContainer
                        } else {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        }
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (message.startsWith("Erreur")) {
                            MaterialTheme.colorScheme.onErrorContainer
                        } else {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        }
                    )
                }
            }
            
            // Auto-hide message after 5 seconds
            LaunchedEffect(message) {
                kotlinx.coroutines.delay(5000)
                exportMessage = null
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Informations sur les formats
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Format des fichiers CSV",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "• Encodage UTF-8 pour la compatibilité des caractères spéciaux\n" +
                            "• Séparateur virgule (,) standard\n" +
                            "• En-têtes de colonnes inclus\n" +
                            "• Compatible Excel, LibreOffice, Google Sheets\n" +
                            "• Dates au format YYYY-MM-DD HH:mm:ss",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun StatsCard(
    title: String,
    count: Int,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        modifier = Modifier.width(100.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun ExportButton(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    enabled: Boolean,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            Icon(
                Icons.Default.Download,
                contentDescription = null
            )
        }
    }
}
