package com.example.appstock.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.example.appstock.ui.utils.getProductViewModel
import com.example.appstock.ui.utils.getCustomerViewModel
import com.example.appstock.ui.utils.getSaleViewModel
import com.example.appstock.ui.utils.getInvoiceViewModel
import com.example.appstock.ui.utils.getSaleHeaderViewModel
import com.example.appstock.data.SaleHeader
import com.example.appstock.data.Product
import com.example.appstock.data.Customer
import com.example.appstock.data.Sale
import com.example.appstock.data.Invoice
import com.example.appstock.utils.ExcelExporter
import java.text.SimpleDateFormat
import java.util.*

/**
 * Reports screen for analytics and statistics - Updated for multi-product sales
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen() {
    val context = LocalContext.current
    val productViewModel = getProductViewModel()
    val customerViewModel = getCustomerViewModel()
    val saleViewModel = getSaleViewModel()
    val saleHeaderViewModel = getSaleHeaderViewModel()
    val invoiceViewModel = getInvoiceViewModel()
    
    val allProducts by productViewModel.allProducts.observeAsState(emptyList<Product>())
    val allCustomers by customerViewModel.allCustomers.observeAsState(emptyList<Customer>())
    val allSales by saleViewModel.allSales.observeAsState(emptyList<Sale>())
    val allSaleHeaders by saleHeaderViewModel.allSaleHeaders.observeAsState(emptyList<SaleHeader>())
    val allInvoices by invoiceViewModel.allInvoices.observeAsState(emptyList<Invoice>())
    
    var selectedReport by remember { mutableStateOf<String?>(null) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Rapports et Statistiques",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        if (selectedReport == null) {
            // Reports categories
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    ReportCategoryCard(
                        title = "Rapport de ventes",
                        description = "Analyser les performances de vente par période",
                        icon = Icons.Default.TrendingUp,
                        onClick = { selectedReport = "sales" }
                    )
                }
                
                item {
                    ReportCategoryCard(
                        title = "Rapport d'inventaire",
                        description = "État des stocks et valeurs d'inventaire",
                        icon = Icons.Default.Inventory,
                        onClick = { selectedReport = "inventory" }
                    )
                }
                
                item {
                    ReportCategoryCard(
                        title = "Rapport financier",
                        description = "Chiffre d'affaires et bénéfices",
                        icon = Icons.Default.AccountBalance,
                        onClick = { selectedReport = "financial" }
                    )
                }
                
                item {
                    ReportCategoryCard(
                        title = "Rapport clients",
                        description = "Analyse de la clientèle et fidélité",
                        icon = Icons.Default.People,
                        onClick = { selectedReport = "customers" }
                    )
                }
                
                item {
                    ReportCategoryCard(
                        title = "Rapport produits",
                        description = "Performance des produits les plus vendus",
                        icon = Icons.Default.Star,
                        onClick = { selectedReport = "products" }
                    )
                }
                
                item {
                    ReportCategoryCard(
                        title = "Alertes et stocks",
                        description = "Produits en rupture ou stock faible",
                        icon = Icons.Default.Warning,
                        onClick = { selectedReport = "alerts" }
                    )
                }
                
                item {
                    ReportCategoryCard(
                        title = "Export de données",
                        description = "Exporter toutes les données en Excel",
                        icon = Icons.Default.FileDownload,
                        onClick = { selectedReport = "export" }
                    )
                }
            }
        } else {
            // Show specific report
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { selectedReport = null }
                ) {
                    Text("← Retour")
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                Text(
                    text = getReportTitle(selectedReport!!),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.weight(1f))
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            when (selectedReport) {
                "sales" -> SalesReportContent(allSaleHeaders)
                "inventory" -> InventoryReportContent(allProducts)
                "financial" -> FinancialReportContent(allSales, allInvoices)
                "customers" -> CustomersReportContent(allCustomers, allSales)
                "products" -> ProductsReportContent(allProducts, allSales)
                "alerts" -> AlertsReportContent(allProducts)
                "export" -> ExportReportContent(context, allProducts, allCustomers, allSales, allInvoices)
            }
        }
    }
}

@Composable
fun SalesReportContent(saleHeaders: List<SaleHeader>) {
    val today = Calendar.getInstance()
    val thisMonth = saleHeaders.filter { 
        val saleCalendar = Calendar.getInstance().apply { timeInMillis = it.saleDate }
        saleCalendar.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
        saleCalendar.get(Calendar.YEAR) == today.get(Calendar.YEAR)
    }
    
    val thisWeek = saleHeaders.filter { 
        val saleCalendar = Calendar.getInstance().apply { timeInMillis = it.saleDate }
        val weekDiff = (today.timeInMillis - saleCalendar.timeInMillis) / (1000 * 60 * 60 * 24)
        weekDiff <= 7
    }
    
    Card {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Résumé des ventes multi-produits", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Cette semaine", style = MaterialTheme.typography.bodyMedium)
                    Text("${thisWeek.size} ventes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("${String.format("%.2f", thisWeek.sumOf { saleHeader -> saleHeader.totalAmount })} Dhs", style = MaterialTheme.typography.bodyMedium)
                }
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Ce mois", style = MaterialTheme.typography.bodyMedium)
                    Text("${thisMonth.size} ventes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("${String.format("%.2f", thisMonth.sumOf { saleHeader -> saleHeader.totalAmount })} Dhs", style = MaterialTheme.typography.bodyMedium)
                }
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Total", style = MaterialTheme.typography.bodyMedium)
                    Text("${saleHeaders.size} ventes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("${String.format("%.2f", saleHeaders.sumOf { saleHeader -> saleHeader.totalAmount })} Dhs", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
    
    Spacer(modifier = Modifier.height(16.dp))
    
    Text("Dernières ventes multi-produits", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(8.dp))
    
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(saleHeaders.sortedByDescending { it.dateTimestamp }.take(20)) { saleHeader ->
            Card {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Vente Multi-Produit #${saleHeader.id}", fontWeight = FontWeight.Medium)
                        Text("Client ID: ${saleHeader.customerId ?: "Client direct"}", style = MaterialTheme.typography.bodySmall)
                        Text("${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(saleHeader.dateTimestamp))}", style = MaterialTheme.typography.bodySmall)
                        if (saleHeader.notes?.isNotEmpty() == true) {
                            Text("Notes: ${saleHeader.notes}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("${String.format("%.2f", saleHeader.totalAmount)} Dhs", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun InventoryReportContent(products: List<com.example.appstock.data.Product>) {
    val totalValue = products.sumOf { (it.price * it.stock).toDouble() }
    val lowStockProducts = products.filter { it.stock <= 5 }
    val outOfStockProducts = products.filter { it.stock == 0 }
    
    Card {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("État de l'inventaire", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Produits totaux", style = MaterialTheme.typography.bodyMedium)
                    Text("${products.size}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Valeur totale", style = MaterialTheme.typography.bodyMedium)
                    Text("${String.format("%.2f", totalValue)}Dhs", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Stock faible", style = MaterialTheme.typography.bodyMedium)
                    Text("${lowStockProducts.size}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
    
    if (lowStockProducts.isNotEmpty()) {
        Spacer(modifier = Modifier.height(16.dp))
        Text("Produits avec stock faible", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(lowStockProducts) { product ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (product.stock == 0) MaterialTheme.colorScheme.errorContainer 
                        else MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (product.stock == 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(product.name, fontWeight = FontWeight.Medium)
                            Text("Stock: ${product.stock}", style = MaterialTheme.typography.bodySmall)
                        }
                        Text("${String.format("%.2f", product.price)}Dhs", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun FinancialReportContent(sales: List<com.example.appstock.data.Sale>, invoices: List<com.example.appstock.data.Invoice>) {
    val totalRevenue = sales.sumOf { it.totalPrice }
    val totalInvoiced = invoices.sumOf { it.totalAmount }
    val paidInvoices = invoices.filter { it.isPaid }
    val unpaidInvoices = invoices.filter { !it.isPaid }
    
    Card {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Résumé financier", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Chiffre d'affaires total")
                    Text("${String.format("%.2f", totalRevenue)}Dhs", fontWeight = FontWeight.Bold)
                }
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total facturé")
                    Text("${String.format("%.2f", totalInvoiced)}Dhs", fontWeight = FontWeight.Bold)
                }
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Factures payées")
                    Text("${String.format("%.2f", paidInvoices.sumOf { it.totalAmount })}Dhs", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Factures impayées")
                    Text("${String.format("%.2f", unpaidInvoices.sumOf { it.totalAmount })}Dhs", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun CustomersReportContent(customers: List<com.example.appstock.data.Customer>, sales: List<com.example.appstock.data.Sale>) {
    val customerSales = sales.groupBy { it.customerId }
    val topCustomers = customerSales.map { (customerId, customerSalesList) ->
        val customer = customers.find { it.id == customerId }
        Triple(customer, customerSalesList.size, customerSalesList.sumOf { it.totalPrice })
    }.sortedByDescending { it.third }.take(10)
    
    Card {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Analyse clientèle", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Total clients", style = MaterialTheme.typography.bodyMedium)
                    Text("${customers.size}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Clients actifs", style = MaterialTheme.typography.bodyMedium)
                    Text("${customerSales.size}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
    
    if (topCustomers.isNotEmpty()) {
        Spacer(modifier = Modifier.height(16.dp))
        Text("Top clients", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(topCustomers) { (customer, salesCount, totalAmount) ->
                Card {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(customer?.name ?: "Client inconnu", fontWeight = FontWeight.Medium)
                            Text("$salesCount vente(s)", style = MaterialTheme.typography.bodySmall)
                        }
                        Text("${String.format("%.2f", totalAmount)}Dhs", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ProductsReportContent(products: List<com.example.appstock.data.Product>, sales: List<com.example.appstock.data.Sale>) {
    val productSales = sales.groupBy { it.productId }
    val topProducts = productSales.map { (productId, productSalesList) ->
        val product = products.find { it.id == productId }
        Triple(product, productSalesList.sumOf { it.quantity }, productSalesList.sumOf { it.totalPrice })
    }.sortedByDescending { it.third }.take(10)
    
    Card {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Performance produits", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            
            Text("Produits les plus vendus", style = MaterialTheme.typography.bodyMedium)
        }
    }
    
    if (topProducts.isNotEmpty()) {
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(topProducts) { (product, quantitySold, totalRevenue) ->
                Card {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(product?.name ?: "Produit inconnu", fontWeight = FontWeight.Medium)
                            Text("$quantitySold unité(s) vendues", style = MaterialTheme.typography.bodySmall)
                        }
                        Text("${String.format("%.2f", totalRevenue)}Dhs", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AlertsReportContent(products: List<com.example.appstock.data.Product>) {
    val outOfStock = products.filter { it.stock == 0 }
    val lowStock = products.filter { it.stock in 1..5 }
    
    if (outOfStock.isEmpty() && lowStock.isEmpty()) {
        Card {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Aucune alerte de stock !", fontWeight = FontWeight.Bold)
                Text("Tous vos produits ont un stock suffisant.", style = MaterialTheme.typography.bodyMedium)
            }
        }
    } else {
        if (outOfStock.isNotEmpty()) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("🚨 Produits en rupture (${outOfStock.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    outOfStock.forEach { product ->
                        Text("• ${product.name}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        if (lowStock.isNotEmpty()) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("⚠️ Stock faible (${lowStock.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    lowStock.forEach { product ->
                        Text("• ${product.name} (${product.stock} restant)", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

@Composable
fun ExportReportContent(
    context: android.content.Context, 
    products: List<com.example.appstock.data.Product>,
    customers: List<com.example.appstock.data.Customer>,
    sales: List<com.example.appstock.data.Sale>,
    invoices: List<com.example.appstock.data.Invoice>
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Export de données", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Exportez vos données en fichiers Excel pour analyse externe ou sauvegarde.")
            }
        }
        
        Button(
            onClick = { ExcelExporter.exportProducts(context, products) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.FileDownload, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Exporter les produits (${products.size})")
        }
        
        Button(
            onClick = { ExcelExporter.exportCustomers(context, customers) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.FileDownload, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Exporter les clients (${customers.size})")
        }
        
        Button(
            onClick = { ExcelExporter.exportSales(context, sales) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.FileDownload, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Exporter les ventes (${sales.size})")
        }
        
        OutlinedButton(
            onClick = { 
                ExcelExporter.exportProducts(context, products)
                ExcelExporter.exportCustomers(context, customers)
                ExcelExporter.exportSales(context, sales)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Tout exporter")
        }
    }
}

fun getReportTitle(reportType: String): String = when (reportType) {
    "sales" -> "Rapport de ventes"
    "inventory" -> "Rapport d'inventaire" 
    "financial" -> "Rapport financier"
    "customers" -> "Rapport clients"
    "products" -> "Rapport produits"
    "alerts" -> "Alertes et stocks"
    "export" -> "Export de données"
    else -> "Rapport"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportCategoryCard(
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

