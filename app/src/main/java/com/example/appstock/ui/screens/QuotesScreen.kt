package com.example.appstock.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Transform
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
/**
 * Quotes screen for managing quotes
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuotesScreen() {
    var showNewQuoteDialog by remember { mutableStateOf(false) }
    
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
                text = "Devis",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            
            FloatingActionButton(
                onClick = { showNewQuoteDialog = true },
                modifier = Modifier.size(56.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Nouveau devis")
            }
        }
        
        // Quick actions
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                QuoteActionCard(
                    title = "Créer un devis",
                    description = "Générer un nouveau devis pour un client",
                    icon = Icons.Default.Description,
                    onClick = { /* TODO: Handle create quote */ }
                )
            }
            
            item {
                QuoteActionCard(
                    title = "Devis en attente",
                    description = "Voir les devis en cours de validation",
                    icon = Icons.Default.Schedule,
                    onClick = { /* TODO: Handle pending quotes */ }
                )
            }
            
            item {
                QuoteActionCard(
                    title = "Devis acceptés",
                    description = "Consulter les devis validés par les clients",
                    icon = Icons.Filled.CheckCircle,
                    onClick = { /* TODO: Handle accepted quotes */ }
                )
            }
            
            item {
                QuoteActionCard(
                    title = "Convertir en facture",
                    description = "Transformer un devis accepté en facture",
                    icon = Icons.Default.Transform,
                    onClick = { /* TODO: Handle quote to invoice */ }
                )
            }
            
            item {
                QuoteActionCard(
                    title = "Exporter en PDF",
                    description = "Générer et partager des devis PDF",
                    icon = Icons.Default.PictureAsPdf,
                    onClick = { /* TODO: Handle PDF export */ }
                )
            }
            
            item {
                QuoteActionCard(
                    title = "Historique des devis",
                    description = "Voir tous les devis créés",
                    icon = Icons.Default.History,
                    onClick = { /* TODO: Handle quote history */ }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuoteActionCard(
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

