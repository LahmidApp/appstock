package com.example.appstock.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Euro
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModelProvider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import com.example.appstock.viewmodel.ProductViewModel
import com.example.appstock.viewmodel.CustomerViewModel
import com.example.appstock.viewmodel.SaleViewModel
import com.example.appstock.viewmodel.SaleHeaderViewModel
import com.example.appstock.viewmodel.InvoiceViewModel
import com.example.appstock.data.Product
import com.example.appstock.data.Customer
import com.example.appstock.data.Sale
import com.example.appstock.data.SaleHeader
import com.example.appstock.data.Invoice
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavController
import com.example.appstock.ui.utils.getProductViewModel
import com.example.appstock.ui.utils.getCustomerViewModel
import com.example.appstock.ui.utils.getSaleViewModel
import com.example.appstock.ui.utils.getSaleHeaderViewModel
import com.example.appstock.ui.utils.getInvoiceViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Dashboard screen showing key metrics and quick actions - Updated for multi-product sales
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController) {
    val productViewModel: ProductViewModel = getProductViewModel()
    val customerViewModel: CustomerViewModel = getCustomerViewModel()
    val saleViewModel: SaleViewModel = getSaleViewModel()
    val saleHeaderViewModel: SaleHeaderViewModel = getSaleHeaderViewModel()
    val invoiceViewModel: InvoiceViewModel = getInvoiceViewModel()
    
    val allProducts by productViewModel.allProducts.observeAsState(emptyList<Product>())
    val allCustomers by customerViewModel.allCustomers.observeAsState(emptyList<Customer>())
    val allSales by saleViewModel.allSales.observeAsState(emptyList<Sale>())
    val allSaleHeaders by saleHeaderViewModel.allSaleHeaders.observeAsState(emptyList<SaleHeader>())
    val allInvoices by invoiceViewModel.allInvoices.observeAsState(emptyList<Invoice>())
    
    // Calculs en temps réel
    val totalProducts = allProducts.size
    val totalCustomers = allCustomers.size
    val lowStockProducts = allProducts.filter { it.stock <= 5 }
    val outOfStockProducts = allProducts.filter { it.stock == 0 }
    
    // Calculs financiers - utilisation des ventes multi-produits
    val now = System.currentTimeMillis()
    val oneDayMillis = 24 * 60 * 60 * 1000L
    val oneMonthMillis = 30 * oneDayMillis
    val todayStart = now - (now % oneDayMillis)
    
    // Utilisation des nouvelles ventes multi-produits pour les calculs
    val todaySaleHeaders = allSaleHeaders.filter { saleHeader -> saleHeader.saleDate >= todayStart }
    val dailyRevenue = todaySaleHeaders.sumOf { saleHeader -> saleHeader.totalAmount }
    val monthlyRevenue = allSaleHeaders.filter { saleHeader -> 
        saleHeader.saleDate >= (now - oneMonthMillis)
    }.sumOf { saleHeader -> saleHeader.totalAmount }
    
    // Calculs legacy pour comparaison (à supprimer progressivement)
    val todayLegacySales = allSales.filter { it.saleDate >= todayStart }
    val dailyLegacyRevenue = todayLegacySales.sumOf { it.totalPrice }
    
    val unpaidInvoices = allInvoices.filter { !it.isPaid }
    val totalUnpaidAmount = unpaidInvoices.sumOf { it.totalAmount }
    
    val totalInventoryValue = allProducts.sumOf { (it.price * it.stock).toDouble() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Tableau de bord",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Métriques principales
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricCard(
                        title = "Produits",
                        value = totalProducts.toString(),
                        icon = Icons.Default.Inventory,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Clients",
                        value = totalCustomers.toString(),
                        icon = Icons.Default.People,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricCard(
                        title = "CA Journalier",
                        value = "${String.format("%.2f", dailyRevenue)}Dhs",
                        icon = Icons.Default.Euro,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "CA Mensuel",
                        value = "${String.format("%.2f", monthlyRevenue)}Dhs",
                        icon = Icons.Default.AttachMoney,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricCard(
                        title = "Valeur Stock",
                        value = "${String.format("%.2f", totalInventoryValue)}Dhs",
                        icon = Icons.Default.Inventory,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Impayés",
                        value = "${String.format("%.2f", totalUnpaidAmount)}Dhs",
                        icon = Icons.Default.Warning,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Alertes stock
            if (lowStockProducts.isNotEmpty() || outOfStockProducts.isNotEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                "⚠️ Alertes Stock",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            if (outOfStockProducts.isNotEmpty()) {
                                Text("${outOfStockProducts.size} produits en rupture de stock")
                            }
                            if (lowStockProducts.isNotEmpty()) {
                                Text("${lowStockProducts.size} produits avec stock faible (≤5)")
                            }
                        }
                    }
                }
            }

            // Actions rapides
            item {
                Text(
                    "Actions rapides",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(getQuickActions(navController)) { action ->
                        QuickActionCard(action = action)
                    }
                }
            }

            // Produits avec stock faible
            if (lowStockProducts.isNotEmpty()) {
                item {
                    Text(
                        "Produits à réapprovisionner",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                items(lowStockProducts.take(5)) { product ->
                    Card {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
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
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickActionCard(action: QuickAction) {
    Card(
        onClick = { action.onClick() },
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = action.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = 16.dp)
            )
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = action.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = action.description,
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

data class QuickAction(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val onClick: () -> Unit
)

fun getQuickActions(navController: NavController): List<QuickAction> {
    return listOf(
        QuickAction(
            icon = Icons.Default.Add,
            title = "Ajouter produit",
            description = "Nouveau produit",
            onClick = { navController.navigate("products") }
        ),
        QuickAction(
            icon = Icons.Default.ShoppingCart,
            title = "Nouvelle vente",
            description = "Enregistrer vente",
            onClick = { navController.navigate("sales") }
        ),
        QuickAction(
            icon = Icons.Default.Receipt,
            title = "Créer facture",
            description = "Nouvelle facture",
            onClick = { navController.navigate("invoices") }
        ),
        QuickAction(
            icon = Icons.Default.People,
            title = "Ajouter client",
            description = "Nouveau client",
            onClick = { navController.navigate("customers") }
        )
    )
}

