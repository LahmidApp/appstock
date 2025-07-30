package com.example.appstock.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons // General Icons import
// Import specific icons from the 'filled' theme
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.PersonAdd // Already had this
import androidx.compose.material.icons.filled.Schedule // Already had this
import androidx.compose.material.icons.filled.FileDownload // Already had this
import androidx.compose.material.icons.filled.ChevronRight // Already had this
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Customers screen for managing customer information
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomersScreen() {
    var showAddCustomerDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

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
                text = "Clients",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            FloatingActionButton(
                onClick = { showAddCustomerDialog = true },
                modifier = Modifier.size(56.dp)
            ) {
                // Use Icons.Filled.Add
                Icon(Icons.Filled.Add, contentDescription = "Ajouter un client")
            }
        }

        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Rechercher un client") },
            // Use Icons.Filled.Search
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Quick actions
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                CustomerActionCard(
                    title = "Ajouter un client",
                    description = "Créer une nouvelle fiche client",
                    // Icons.Filled.PersonAdd is correct (assuming you have the import)
                    icon = Icons.Filled.PersonAdd,
                    onClick = { showAddCustomerDialog = true }
                )
            }

            item {
                CustomerActionCard(
                    title = "Clients récents",
                    description = "Voir les derniers clients ajoutés",
                    // Icons.Filled.Schedule is correct
                    icon = Icons.Filled.Schedule,
                    onClick = { /* TODO: Handle recent customers */ }
                )
            }

            item {
                CustomerActionCard(
                    title = "Clients fidèles",
                    description = "Consulter les meilleurs clients",
                    // Use Icons.Filled.Star
                    icon = Icons.Filled.Star,
                    onClick = { /* TODO: Handle loyal customers */ }
                )
            }

            item {
                CustomerActionCard(
                    title = "Exporter la liste",
                    description = "Exporter les données clients en Excel",
                    // Icons.Filled.FileDownload is correct
                    icon = Icons.Filled.FileDownload,
                    onClick = { /* TODO: Handle export customers */ }
                )
            }
        }
    }

    // Add customer dialog
    if (showAddCustomerDialog) {
        AddCustomerDialog(
            onDismiss = { showAddCustomerDialog = false },
            onConfirm = { name, email, phone, address ->
                // TODO: Add customer logic
                showAddCustomerDialog = false
            }
        )
    }
}

// CustomerActionCard and AddCustomerDialog composables remain the same
// but ensure the icons passed to CustomerActionCard are correctly sourced

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerActionCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector, // This type is correct
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
                imageVector = icon, // Receives the icon from the caller
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
                // Icons.Filled.ChevronRight is correct
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AddCustomerDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ajouter un client") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nom du client *") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Téléphone") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Adresse") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isNotEmpty()) {
                        onConfirm(name, email, phone, address)
                    }
                }
            ) {
                Text("Ajouter")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}
