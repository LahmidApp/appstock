package com.example.appstock.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning

/**
 * Reports screen for analytics and statistics
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen() {
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
        
        // Reports categories
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                ReportCategoryCard(
                    title = "Rapports de ventes",
                    description = "Analyser les performances de vente",
                    icon = Icons.Default.TrendingUp,
                    onClick = { /* TODO: Handle sales reports */ }
                )
            }
            
            item {
                ReportCategoryCard(
                    title = "Gestion des stocks",
                    description = "Suivre les niveaux de stock et les mouvements",
                    icon = Icons.Default.Inventory,
                    onClick = { /* TODO: Handle inventory reports */ }
                )
            }
            
            item {
                ReportCategoryCard(
                    title = "Analyse financière",
                    description = "Revenus, dépenses et bénéfices",
                    icon = Icons.Default.AccountBalance,
                    onClick = { /* TODO: Handle financial reports */ }
                )
            }
            
            item {
                ReportCategoryCard(
                    title = "Clients et fidélisation",
                    description = "Analyser le comportement des clients",
                    icon = Icons.Default.People,
                    onClick = { /* TODO: Handle customer reports */ }
                )
            }
            
            item {
                ReportCategoryCard(
                    title = "Produits populaires",
                    description = "Top des ventes et tendances",
                    icon = Icons.Filled.Star,
                    onClick = { /* TODO: Handle product reports */ }
                )
            }
            
            item {
                ReportCategoryCard(
                    title = "Alertes de stock",
                    description = "Produits en rupture ou stock faible",
                    icon = Icons.Filled.Warning,
                    onClick = { /* TODO: Handle stock alerts */ }
                )
            }
            
            item {
                ReportCategoryCard(
                    title = "Exporter les données",
                    description = "Télécharger les rapports en Excel/PDF",
                    icon = Icons.Default.FileDownload,
                    onClick = { /* TODO: Handle data export */ }
                )
            }
        }
    }
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

