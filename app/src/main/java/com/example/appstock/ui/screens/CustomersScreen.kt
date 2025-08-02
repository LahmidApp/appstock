package com.example.appstock.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appstock.data.Customer
import com.example.appstock.viewmodel.CustomerViewModel
import com.example.appstock.utils.ExcelExporter
import com.example.appstock.ui.utils.getCustomerViewModel
import androidx.compose.material.icons.Icons // General Icons import
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.PersonAdd // Already had this
import androidx.compose.material.icons.filled.Schedule // Already had this
import androidx.compose.material.icons.filled.FileDownload // Already had this
import androidx.compose.material.icons.filled.ChevronRight // Already had this
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * Customers screen for managing customer information
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomersScreen(
    customerViewModel: CustomerViewModel = getCustomerViewModel()
) {
    val context = LocalContext.current
    val allCustomers by customerViewModel.allCustomers.observeAsState(emptyList())
    val recentCustomers by customerViewModel.recentCustomers.observeAsState(emptyList())
    val loyalCustomers by customerViewModel.loyalCustomers.observeAsState(emptyList())
    var showAddCustomerDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var showRecentSheet by remember { mutableStateOf(false) }
    var showLoyalSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    // Filtered list
    val filteredCustomers = if (searchQuery.isBlank()) allCustomers else allCustomers.filter { customer ->
        customer.name.contains(searchQuery, ignoreCase = true) ||
        customer.email?.contains(searchQuery, ignoreCase = true) == true ||
        customer.phoneNumber?.contains(searchQuery, ignoreCase = true) == true
    }

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
                Icon(Icons.Filled.Add, contentDescription = "Ajouter un client")
            }
        }

        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Rechercher un client") },
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
                    icon = Icons.Filled.PersonAdd,
                    onClick = { showAddCustomerDialog = true }
                )
            }
            item {
                CustomerActionCard(
                    title = "Clients récents",
                    description = "Voir les derniers clients ajoutés",
                    icon = Icons.Filled.Schedule,
                    onClick = { showRecentSheet = true }
                )
            }
            item {
                CustomerActionCard(
                    title = "Clients fidèles",
                    description = "Consulter les meilleurs clients",
                    icon = Icons.Filled.Star,
                    onClick = { showLoyalSheet = true }
                )
            }
            item {
                CustomerActionCard(
                    title = "Exporter la liste",
                    description = "Exporter les données clients en Excel",
                    icon = Icons.Filled.FileDownload,
                    onClick = {
                        ExcelExporter.exportCustomers(context, allCustomers)
                    }
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Liste des clients",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredCustomers) { customer ->
                CustomerListItem(customer)
            }
            if (filteredCustomers.isEmpty()) {
                item {
                    Text("Aucun client trouvé.", modifier = Modifier.padding(16.dp))
                }
            }
        }
    }

    // Add customer dialog
    if (showAddCustomerDialog) {
        AddCustomerDialog(
            onDismiss = { showAddCustomerDialog = false },
            onConfirm = { name, email, phone, address ->
                customerViewModel.addCustomer(
                    Customer(
                        name = name,
                        email = email.takeIf { it.isNotBlank() },
                        phoneNumber = phone.takeIf { it.isNotBlank() },
                        address = address.takeIf { it.isNotBlank() },
                        createdAt = System.currentTimeMillis()
                    )
                )
                showAddCustomerDialog = false
            }
        )
    }

    // BottomSheet for recent customers
    if (showRecentSheet) {
        ModalBottomSheet(
            onDismissRequest = { showRecentSheet = false },
            sheetState = sheetState
        ) {
            Text(
                "Clients récents",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
            LazyColumn(
                modifier = Modifier.fillMaxHeight(0.6f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(recentCustomers) { customer ->
                    CustomerListItem(customer)
                }
                if (recentCustomers.isEmpty()) {
                    item { Text("Aucun client récent.", modifier = Modifier.padding(16.dp)) }
                }
            }
        }
    }

    // BottomSheet for loyal customers
    if (showLoyalSheet) {
        ModalBottomSheet(
            onDismissRequest = { showLoyalSheet = false },
            sheetState = sheetState
        ) {
            Text(
                "Clients fidèles",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
            LazyColumn(
                modifier = Modifier.fillMaxHeight(0.6f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(loyalCustomers) { customer ->
                    CustomerListItem(customer)
                }
                if (loyalCustomers.isEmpty()) {
                    item { Text("Aucun client fidèle.", modifier = Modifier.padding(16.dp)) }
                }
            }
        }
    }
}
@Composable
fun CustomerListItem(customer: Customer) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(customer.name, fontWeight = FontWeight.Bold)
            customer.email?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
            customer.phoneNumber?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
            customer.address?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
            customer.ice?.let { Text("ICE: $it", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary) }
        }
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
