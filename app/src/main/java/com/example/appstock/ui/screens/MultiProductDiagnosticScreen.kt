package com.example.appstock.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.appstock.ui.utils.*
import com.example.appstock.utils.SalesMigrationHelper
import kotlinx.coroutines.launch

/**
 * Écran de diagnostic pour vérifier l'adaptation du code à la logique multi-produits
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultiProductDiagnosticScreen() {
    val scope = rememberCoroutineScope()
    
    val saleViewModel = getSaleViewModel()
    val saleHeaderViewModel = getSaleHeaderViewModel()
    val productViewModel = getProductViewModel()
    val customerViewModel = getCustomerViewModel()
    val invoiceViewModel = getInvoiceViewModel()
    
    val allSales by saleViewModel.allSales.observeAsState(emptyList())
    val allSaleHeaders by saleHeaderViewModel.allSaleHeaders.observeAsState(emptyList())
    val allProducts by productViewModel.allProducts.observeAsState(emptyList())
    val allCustomers by customerViewModel.allCustomers.observeAsState(emptyList())
    val allInvoices by invoiceViewModel.allInvoices.observeAsState(emptyList())
    
    var diagnosticReport by remember { mutableStateOf<DiagnosticReport?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    
    // Calculer le diagnostic
    LaunchedEffect(allSales, allSaleHeaders, allProducts, allCustomers, allInvoices) {
        isLoading = true
        try {
            val consistencyReport = SalesMigrationHelper.validateDataConsistency(saleViewModel, saleHeaderViewModel)
            
            diagnosticReport = DiagnosticReport(
                legacySalesCount = allSales.size,
                multiProductSalesCount = allSaleHeaders.size,
                legacyRevenue = allSales.sumOf { it.totalPrice },
                multiProductRevenue = allSaleHeaders.sumOf { it.totalAmount },
                totalProducts = allProducts.size,
                totalCustomers = allCustomers.size,
                totalInvoices = allInvoices.size,
                consistencyReport = consistencyReport,
                issues = generateIssues(allSales, allSaleHeaders, allProducts)
            )
        } finally {
            isLoading = false
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Diagnostic Multi-Produits",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            diagnosticReport?.let { report ->
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Résumé général
                    item {
                        GeneralSummaryCard(report)
                    }
                    
                    // Analyse de cohérence
                    item {
                        ConsistencyAnalysisCard(report.consistencyReport)
                    }
                    
                    // Problèmes détectés
                    item {
                        IssuesCard(report.issues)
                    }
                    
                    // Recommandations
                    item {
                        RecommendationsCard(report)
                    }
                    
                    // Actions de migration
                    item {
                        MigrationActionsCard(
                            onMigrateAll = {
                                scope.launch {
                                    try {
                                        val migratedCount = SalesMigrationHelper.migrateAllSalesToMultiProduct(
                                            saleViewModel, 
                                            saleHeaderViewModel
                                        )
                                        // Afficher un message de succès
                                    } catch (e: Exception) {
                                        // Afficher un message d'erreur
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GeneralSummaryCard(report: DiagnosticReport) {
    Card {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Résumé Général",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Ventes Legacy", style = MaterialTheme.typography.bodyMedium)
                    Text("${report.legacySalesCount} ventes", fontWeight = FontWeight.Bold)
                    Text("${String.format("%.2f", report.legacyRevenue)} Dhs", color = MaterialTheme.colorScheme.primary)
                }
                
                Column {
                    Text("Ventes Multi-Produits", style = MaterialTheme.typography.bodyMedium)
                    Text("${report.multiProductSalesCount} ventes", fontWeight = FontWeight.Bold)
                    Text("${String.format("%.2f", report.multiProductRevenue)} Dhs", color = MaterialTheme.colorScheme.primary)
                }
            }
            
            Text("Total Produits: ${report.totalProducts}")
            Text("Total Clients: ${report.totalCustomers}")
            Text("Total Factures: ${report.totalInvoices}")
        }
    }
}

@Composable
fun ConsistencyAnalysisCard(consistencyReport: com.example.appstock.utils.DataConsistencyReport) {
    Card {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = if (consistencyReport.isConsistent) Icons.Default.Check else Icons.Default.Warning,
                    contentDescription = null,
                    tint = if (consistencyReport.isConsistent) Color.Green else Color(0xFFFF9800)
                )
                Text(
                    text = "Analyse de Cohérence",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            if (consistencyReport.isConsistent) {
                Text("✅ Les données sont cohérentes entre les deux systèmes")
            } else {
                consistencyReport.getInconsistencyMessage()?.let { message ->
                    Text(
                        text = "⚠️ $message",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun IssuesCard(issues: List<DiagnosticIssue>) {
    Card {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Problèmes Détectés (${issues.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            if (issues.isEmpty()) {
                Text("✅ Aucun problème détecté")
            } else {
                issues.forEach { issue ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = when (issue.severity) {
                                IssueSeverity.ERROR -> Icons.Default.Error
                                IssueSeverity.WARNING -> Icons.Default.Warning
                                IssueSeverity.INFO -> Icons.Default.Info
                            },
                            contentDescription = null,
                            tint = when (issue.severity) {
                                IssueSeverity.ERROR -> Color.Red
                                IssueSeverity.WARNING -> Color(0xFFFF9800)
                                IssueSeverity.INFO -> Color.Blue
                            }
                        )
                        Column {
                            Text(
                                text = issue.title,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = issue.description,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RecommendationsCard(report: DiagnosticReport) {
    Card {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Recommandations",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            val recommendations = generateRecommendations(report)
            
            recommendations.forEach { recommendation ->
                Text("• $recommendation")
            }
        }
    }
}

@Composable
fun MigrationActionsCard(
    onMigrateAll: () -> Unit
) {
    Card {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Actions de Migration",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Button(
                onClick = onMigrateAll,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Migrer toutes les ventes vers le format multi-produits")
            }
            
            Text(
                text = "⚠️ Cette action va dupliquer les données existantes en format multi-produits",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

// Classes de données pour le diagnostic
data class DiagnosticReport(
    val legacySalesCount: Int,
    val multiProductSalesCount: Int,
    val legacyRevenue: Double,
    val multiProductRevenue: Double,
    val totalProducts: Int,
    val totalCustomers: Int,
    val totalInvoices: Int,
    val consistencyReport: com.example.appstock.utils.DataConsistencyReport,
    val issues: List<DiagnosticIssue>
)

data class DiagnosticIssue(
    val title: String,
    val description: String,
    val severity: IssueSeverity
)

enum class IssueSeverity {
    INFO, WARNING, ERROR
}

private fun generateIssues(
    legacySales: List<com.example.appstock.data.Sale>,
    multiProductSales: List<com.example.appstock.data.SaleHeader>,
    products: List<com.example.appstock.data.Product>
): List<DiagnosticIssue> {
    val issues = mutableListOf<DiagnosticIssue>()
    
    if (legacySales.isNotEmpty() && multiProductSales.isEmpty()) {
        issues.add(
            DiagnosticIssue(
                title = "Migration incomplète",
                description = "Des ventes legacy existent mais aucune vente multi-produits n'a été créée",
                severity = IssueSeverity.WARNING
            )
        )
    }
    
    if (legacySales.isNotEmpty() && multiProductSales.isNotEmpty()) {
        issues.add(
            DiagnosticIssue(
                title = "Double système",
                description = "Les deux systèmes coexistent, risque de confusion",
                severity = IssueSeverity.INFO
            )
        )
    }
    
    val lowStockProducts = products.filter { it.stock <= 5 }
    if (lowStockProducts.isNotEmpty()) {
        issues.add(
            DiagnosticIssue(
                title = "Stock faible détecté",
                description = "${lowStockProducts.size} produits ont un stock <= 5",
                severity = IssueSeverity.WARNING
            )
        )
    }
    
    return issues
}

private fun generateRecommendations(report: DiagnosticReport): List<String> {
    val recommendations = mutableListOf<String>()
    
    if (report.legacySalesCount > 0 && report.multiProductSalesCount == 0) {
        recommendations.add("Migrer les ventes legacy vers le nouveau système multi-produits")
    }
    
    if (report.legacySalesCount > 0 && report.multiProductSalesCount > 0) {
        recommendations.add("Finaliser la migration et supprimer les anciennes ventes après vérification")
    }
    
    recommendations.add("Utiliser exclusivement le système multi-produits pour les nouvelles ventes")
    recommendations.add("Adapter tous les rapports pour utiliser les nouvelles ventes multi-produits")
    
    return recommendations
}
