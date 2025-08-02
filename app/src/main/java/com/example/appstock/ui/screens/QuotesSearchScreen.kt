package com.example.appstock.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.appstock.data.Quote
import com.example.appstock.data.Customer
import com.example.appstock.ui.utils.getQuoteViewModel
import com.example.appstock.ui.utils.getCustomerViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Écran de recherche et gestion des devis avec barre de recherche
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuotesSearchScreen() {
    val quoteViewModel = getQuoteViewModel()
    val customerViewModel = getCustomerViewModel()
    
    val allQuotes by quoteViewModel.allQuotes.observeAsState(emptyList())
    val allCustomers by customerViewModel.allCustomers.observeAsState(emptyList())
    
    var searchText by remember { mutableStateOf("") }
    var showOnlyPending by remember { mutableStateOf(false) }
    var sortOrder by remember { mutableStateOf("date_desc") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Recherche de Devis",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Barre de recherche
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text("Rechercher par client, montant, statut...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (searchText.isNotEmpty()) {
                    IconButton(onClick = { searchText = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Effacer")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Filtres
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Filtre statut en attente
            FilterChip(
                onClick = { showOnlyPending = !showOnlyPending },
                label = { Text("En attente uniquement") },
                selected = showOnlyPending,
                leadingIcon = if (showOnlyPending) {
                    { Icon(Icons.Default.Check, contentDescription = null) }
                } else null
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Tri
            var showSortMenu by remember { mutableStateOf(false) }
            
            Box {
                IconButton(onClick = { showSortMenu = true }) {
                    Icon(Icons.Default.Sort, contentDescription = "Trier")
                }
                
                DropdownMenu(
                    expanded = showSortMenu,
                    onDismissRequest = { showSortMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Date (récent)") },
                        onClick = {
                            sortOrder = "date_desc"
                            showSortMenu = false
                        },
                        leadingIcon = if (sortOrder == "date_desc") {
                            { Icon(Icons.Default.Check, contentDescription = null) }
                        } else null
                    )
                    DropdownMenuItem(
                        text = { Text("Date (ancien)") },
                        onClick = {
                            sortOrder = "date_asc"
                            showSortMenu = false
                        },
                        leadingIcon = if (sortOrder == "date_asc") {
                            { Icon(Icons.Default.Check, contentDescription = null) }
                        } else null
                    )
                    DropdownMenuItem(
                        text = { Text("Montant (croissant)") },
                        onClick = {
                            sortOrder = "amount_asc"
                            showSortMenu = false
                        },
                        leadingIcon = if (sortOrder == "amount_asc") {
                            { Icon(Icons.Default.Check, contentDescription = null) }
                        } else null
                    )
                    DropdownMenuItem(
                        text = { Text("Montant (décroissant)") },
                        onClick = {
                            sortOrder = "amount_desc"
                            showSortMenu = false
                        },
                        leadingIcon = if (sortOrder == "amount_desc") {
                            { Icon(Icons.Default.Check, contentDescription = null) }
                        } else null
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Liste des devis filtrés
        val filteredQuotes = allQuotes.filter { quote ->
            val customer = allCustomers.find { it.id == quote.customerId }
            val matchesSearch = if (searchText.isEmpty()) true else {
                customer?.name?.contains(searchText, ignoreCase = true) == true ||
                quote.totalAmount.toString().contains(searchText) ||
                quote.status.contains(searchText, ignoreCase = true) ||
                quote.id.toString().contains(searchText)
            }
            
            val matchesStatus = if (showOnlyPending) {
                quote.status.equals("pending", ignoreCase = true) || 
                quote.status.equals("en_attente", ignoreCase = true)
            } else true
            
            matchesSearch && matchesStatus
        }.let { quotes ->
            when (sortOrder) {
                "date_desc" -> quotes.sortedByDescending { it.dateTimestamp }
                "date_asc" -> quotes.sortedBy { it.dateTimestamp }
                "amount_desc" -> quotes.sortedByDescending { it.totalAmount }
                "amount_asc" -> quotes.sortedBy { it.totalAmount }
                else -> quotes.sortedByDescending { it.dateTimestamp }
            }
        }
        
        // Résultats
        Text(
            text = "${filteredQuotes.size} devis trouvé(s)",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        if (filteredQuotes.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredQuotes) { quote ->
                    QuoteSearchItem(
                        quote = quote,
                        customer = allCustomers.find { it.id == quote.customerId }
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Description,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (searchText.isEmpty()) "Aucun devis trouvé" else "Aucun résultat pour \"$searchText\"",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun QuoteSearchItem(
    quote: Quote,
    customer: Customer?
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Devis #${quote.id}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = customer?.name ?: "Client direct",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                    
                    Text(
                        text = "Date: ${dateFormat.format(Date(quote.dateTimestamp))}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "${String.format("%.2f", quote.totalAmount)} Dhs",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    // Statut avec couleur
                    val statusColor = when (quote.status.lowercase()) {
                        "pending", "en_attente" -> MaterialTheme.colorScheme.tertiary
                        "accepted", "accepté" -> MaterialTheme.colorScheme.primary
                        "rejected", "refusé" -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    }
                    
                    Surface(
                        color = statusColor.copy(alpha = 0.2f),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = when (quote.status.lowercase()) {
                                "pending" -> "En attente"
                                "accepted" -> "Accepté"
                                "rejected" -> "Refusé"
                                else -> quote.status
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.bodySmall,
                            color = statusColor,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
