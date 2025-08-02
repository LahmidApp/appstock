package com.example.appstock.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.appstock.ui.utils.*

/**
 * Version simplifiée du Dashboard sans dépendances SaleHeader
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleDashboardScreen() {
    val productViewModel = getProductViewModel()
    val customerViewModel = getCustomerViewModel()
    val quoteViewModel = getQuoteViewModel()
    
    val products by productViewModel.allProducts.observeAsState(initial = emptyList())
    val customers by customerViewModel.allCustomers.observeAsState(initial = emptyList())
    val quotes by quoteViewModel.allQuotes.observeAsState(initial = emptyList())
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // En-tête
        Text(
            text = "Tableau de Bord",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // Statistiques principales
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Statistiques Générales",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatCard("Produits", products.size, Icons.Default.Inventory)
                            StatCard("Clients", customers.size, Icons.Default.People)
                            StatCard("Devis", quotes.size, Icons.Default.Description)
                        }
                    }
                }
            }
            
            item {
                Card {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Actions Rapides",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            QuickActionButton(
                                title = "Nouveau Produit",
                                icon = Icons.Default.Add,
                                onClick = { /* TODO: Navigation */ }
                            )
                            
                            QuickActionButton(
                                title = "Nouveau Client",
                                icon = Icons.Default.PersonAdd,
                                onClick = { /* TODO: Navigation */ }
                            )
                            
                            QuickActionButton(
                                title = "Nouveau Devis",
                                icon = Icons.Default.Description,
                                onClick = { /* TODO: Navigation */ }
                            )
                        }
                    }
                }
            }
            
            item {
                Card {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Données Récentes",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        if (products.isNotEmpty()) {
                            Text(
                                text = "Dernier produit: ${products.firstOrNull()?.name ?: "Aucun"}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        
                        if (customers.isNotEmpty()) {
                            Text(
                                text = "Dernier client: ${customers.firstOrNull()?.name ?: "Aucun"}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        
                        if (quotes.isNotEmpty()) {
                            Text(
                                text = "Derniers devis: ${quotes.size} disponible(s)",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: Int,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        modifier = Modifier.width(100.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun QuickActionButton(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
