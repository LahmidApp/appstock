package com.example.appstock.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.Add // For the FAB
import androidx.compose.material.icons.filled.CheckCircle // For "Factures payées"

/**
 * Invoices screen for managing invoices
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoicesScreen() {
    var showNewInvoiceDialog by remember { mutableStateOf(false) }
    
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
                text = "Factures",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            FloatingActionButton(
                onClick = { showNewInvoiceDialog = true },
                modifier = Modifier.size(56.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Nouvelle facture") // Changed here
            }
        }
        
        // Quick actions
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                InvoiceActionCard(
                    title = "Créer une facture",
                    description = "Générer une nouvelle facture pour un client",
                    icon = Icons.Default.Receipt,
                    onClick = { /* TODO: Handle create invoice */ }
                )
            }
            
            item {
                InvoiceActionCard(
                    title = "Factures en attente",
                    description = "Voir les factures non payées",
                    icon = Icons.Default.Schedule,
                    onClick = { /* TODO: Handle pending invoices */ }
                )
            }
            
            item {
                InvoiceActionCard(
                    title = "Factures payées",
                    description = "Consulter les factures réglées",
                    icon = Icons.Filled.CheckCircle,
                    onClick = { /* TODO: Handle paid invoices */ }
                )
            }
            
            item {
                InvoiceActionCard(
                    title = "Exporter en PDF",
                    description = "Générer et partager des factures PDF",
                    icon = Icons.Default.PictureAsPdf,
                    onClick = { /* TODO: Handle PDF export */ }
                )
            }
            
            item {
                InvoiceActionCard(
                    title = "Historique des factures",
                    description = "Voir toutes les factures créées",
                    icon = Icons.Default.History,
                    onClick = { /* TODO: Handle invoice history */ }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceActionCard(
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

