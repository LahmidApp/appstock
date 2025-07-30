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
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.appstock.viewmodel.ProductViewModelFactory
import com.example.appstock.data.LibraryDatabase
import com.example.appstock.data.ProductRepository
import com.example.appstock.data.Product
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavController

/**
 * Dashboard screen showing key metrics and quick actions
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val productViewModel: ProductViewModel = remember {
        val database = LibraryDatabase.getDatabase(context)
        val repository = ProductRepository(database.productDao())
        ViewModelProvider(
            context as androidx.lifecycle.ViewModelStoreOwner,
            ProductViewModelFactory(repository)
        )[ProductViewModel::class.java]
    }
    val products by productViewModel.products.collectAsState()
    val isLoading by productViewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        productViewModel.loadProducts()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Tableau de bord",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Quick stats cards
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            items(getQuickStats(products)) { stat ->
                StatCard(stat = stat)
            }
        }

        // Quick actions
        Text(
            text = "Actions rapides",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(getQuickActions(navController)) { action ->
                QuickActionCard(action = action)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatCard(stat: QuickStat) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(120.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = stat.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            Column {
                Text(
                    text = stat.value,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stat.label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
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

data class QuickStat(
    val icon: ImageVector,
    val value: String,
    val label: String
)

data class QuickAction(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val onClick: () -> Unit
)

fun getQuickStats(products: List<Product>): List<QuickStat> {
    return listOf(
        QuickStat(
            icon = Icons.Default.Inventory,
            value = products.size.toString(),
            label = "Produits"
        ),
        QuickStat(
            icon = Icons.Default.Warning,
            value = "0", // TODO: Calculate low stock
            label = "Stock faible"
        ),
        QuickStat(
            icon = Icons.Default.TrendingUp,
            value = "0€", // TODO: Calculate today's sales
            label = "Ventes du jour"
        ),
        QuickStat(
            icon = Icons.Default.People,
            value = "0", // TODO: Calculate customers
            label = "Clients"
        )
    )
}

fun getQuickActions(navController: NavController): List<QuickAction> {
    return listOf(
        QuickAction(
            icon = Icons.Default.Add,
            title = "Ajouter un produit",
            description = "Créer un nouveau produit avec code QR",
            onClick = { navController.navigate("products") }
        ),
        QuickAction(
            icon = Icons.Default.ShoppingCart,
            title = "Nouvelle vente",
            description = "Enregistrer une vente rapide",
            onClick = {}
        ),
        QuickAction(
            icon = Icons.Default.Receipt,
            title = "Créer une facture",
            description = "Générer une nouvelle facture",
            onClick = {}
        ),
        QuickAction(
            icon = Icons.Default.Description,
            title = "Nouveau devis",
            description = "Créer un devis pour un client",
            onClick = {}
        ),
        QuickAction(
            icon = Icons.Default.QrCodeScanner,
            title = "Scanner un code",
            description = "Scanner un code QR ou code-barres",
            onClick = {}
        )
    )
}

