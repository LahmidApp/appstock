package com.example.appstock.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Storage
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.example.appstock.ui.utils.getProductViewModel
import com.example.appstock.ui.utils.getCustomerViewModel
import com.example.appstock.ui.utils.getSaleViewModel
import com.example.appstock.ui.utils.getCompanyInfoViewModel
import com.example.appstock.data.CompanyInfo
import com.example.appstock.data.InvoiceType
import androidx.compose.runtime.livedata.observeAsState

/**
 * Settings screen for app configuration
 */
@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val companyInfoViewModel = getCompanyInfoViewModel()
    val currentCompanyInfo by companyInfoViewModel.companyInfo.observeAsState()
    
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var showCompanyInfoDialog by remember { mutableStateOf(false) }
    var showBackupDialog by remember { mutableStateOf(false) }
    var showDeleteDataDialog by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Paramètres",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        if (selectedCategory == null) {
            // Settings categories
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    SettingsCategoryCard(
                        title = "Informations de l'entreprise",
                        description = "Nom, logo, adresse et coordonnées",
                        icon = Icons.Default.Store,
                        onClick = { showCompanyInfoDialog = true }
                    )
                }
                
                item {
                    SettingsCategoryCard(
                        title = "Paramètres des factures",
                        description = "Modèles, numérotation, conditions",
                        icon = Icons.Default.Receipt,
                        onClick = { selectedCategory = "invoices" }
                    )
                }
                
                item {
                    SettingsCategoryCard(
                        title = "Gestion des utilisateurs",
                        description = "Comptes, permissions, accès",
                        icon = Icons.Default.People,
                        onClick = { selectedCategory = "users" }
                    )
                }
                
                item {
                    SettingsCategoryCard(
                        title = "Sauvegarde et sync",
                        description = "Sauvegarde automatique, synchronisation",
                        icon = Icons.Default.CloudSync,
                        onClick = { showBackupDialog = true }
                    )
                }
                
                item {
                    SettingsCategoryCard(
                        title = "Paramètres d'impression",
                        description = "Imprimantes, formats, templates",
                        icon = Icons.Default.Print,
                        onClick = { selectedCategory = "printing" }
                    )
                }
                
                item {
                    SettingsCategoryCard(
                        title = "Notifications",
                        description = "Alertes stock, rappels, notifications",
                        icon = Icons.Default.Notifications,
                        onClick = { selectedCategory = "notifications" }
                    )
                }
                
                item {
                    SettingsCategoryCard(
                        title = "Sécurité et confidentialité",
                        description = "Codes d'accès, données personnelles",
                        icon = Icons.Default.Security,
                        onClick = { selectedCategory = "security" }
                    )
                }
                
                item {
                    SettingsCategoryCard(
                        title = "Thème et interface",
                        description = "Mode sombre, couleurs, langue",
                        icon = Icons.Default.DarkMode,
                        onClick = { selectedCategory = "appearance" }
                    )
                }
                
                item {
                    SettingsCategoryCard(
                        title = "Stockage et données",
                        description = "Espace utilisé, nettoyage, export",
                        icon = Icons.Default.Storage,
                        onClick = { selectedCategory = "storage" }
                    )
                }
                
                item {
                    SettingsCategoryCard(
                        title = "À propos",
                        description = "Version, support, mentions légales",
                        icon = Icons.Default.Info,
                        onClick = { selectedCategory = "about" }
                    )
                }
            }
        } else {
            // Show specific category settings
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { selectedCategory = null }
                ) {
                    Text("← Retour")
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                Text(
                    text = getCategoryTitle(selectedCategory!!),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.weight(1f))
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            when (selectedCategory) {
                "invoices" -> InvoiceSettingsContent()
                "users" -> UserSettingsContent()
                "printing" -> PrintingSettingsContent()
                "notifications" -> NotificationSettingsContent()
                "security" -> SecuritySettingsContent()
                "appearance" -> AppearanceSettingsContent()
                "storage" -> StorageSettingsContent()
                "about" -> AboutContent()
            }
        }
    }

    // Company info dialog
    if (showCompanyInfoDialog) {
        CompanyInfoDialog(
            currentCompanyInfo = currentCompanyInfo,
            onDismiss = { showCompanyInfoDialog = false },
            onSave = { name, address, phone, email, website, ice, ifNumber, patente, rc, bankAccount, logoUri, invoiceType, tvaRate ->
                val companyInfo = CompanyInfo(
                    id = 1L, // ID fixe car une seule entrée
                    companyName = name,
                    address = address,
                    phone = phone,
                    email = email,
                    website = website.ifBlank { null },
                    logoUri = logoUri.ifBlank { null },
                    ice = ice.ifBlank { null },
                    ifNumber = ifNumber.ifBlank { null },
                    patente = patente.ifBlank { null },
                    rc = rc.ifBlank { null },
                    bankAccount = bankAccount.ifBlank { null },
                    invoiceType = invoiceType,
                    tvaRate = tvaRate
                )
                companyInfoViewModel.insertOrUpdate(companyInfo)
                showCompanyInfoDialog = false
            }
        )
    }

    // Backup dialog
    if (showBackupDialog) {
        BackupDialog(
            onDismiss = { showBackupDialog = false },
            onBackup = {
                // TODO: Implement backup functionality
                showBackupDialog = false
            },
            onRestore = {
                // TODO: Implement restore functionality
                showBackupDialog = false
            }
        )
    }
}

@Composable
fun InvoiceSettingsContent() {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Numérotation des factures", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    var prefix by remember { mutableStateOf("FAC") }
                    var startNumber by remember { mutableStateOf("1") }
                    
                    OutlinedTextField(
                        value = prefix,
                        onValueChange = { prefix = it },
                        label = { Text("Préfixe") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = startNumber,
                        onValueChange = { startNumber = it },
                        label = { Text("Numéro de départ") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
        
        item {
            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Conditions de paiement", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    var paymentTerms by remember { mutableStateOf("Paiement à 30 jours") }
                    
                    OutlinedTextField(
                        value = paymentTerms,
                        onValueChange = { paymentTerms = it },
                        label = { Text("Conditions") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            }
        }
    }
}

@Composable
fun UserSettingsContent() {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Gestion des accès", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Fonctionnalité disponible dans la version Pro")
                }
            }
        }
    }
}

@Composable
fun PrintingSettingsContent() {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Format d'impression", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    var selectedFormat by remember { mutableStateOf("A4") }
                    val formats = listOf("A4", "A5", "Letter")
                    
                    formats.forEach { format ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedFormat == format,
                                onClick = { selectedFormat = format }
                            )
                            Text(format)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationSettingsContent() {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Alertes de stock", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    var stockAlerts by remember { mutableStateOf(true) }
                    var lowStockThreshold by remember { mutableStateOf("5") }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Activer les alertes de stock")
                        Switch(
                            checked = stockAlerts,
                            onCheckedChange = { stockAlerts = it }
                        )
                    }
                    
                    if (stockAlerts) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = lowStockThreshold,
                            onValueChange = { lowStockThreshold = it },
                            label = { Text("Seuil d'alerte") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SecuritySettingsContent() {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Verrouillage de l'application", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    var appLock by remember { mutableStateOf(false) }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Code PIN requis")
                        Switch(
                            checked = appLock,
                            onCheckedChange = { appLock = it }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AppearanceSettingsContent() {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Thème", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    var darkMode by remember { mutableStateOf(false) }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Mode sombre")
                        Switch(
                            checked = darkMode,
                            onCheckedChange = { darkMode = it }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StorageSettingsContent() {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Card {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Gestion des données", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Button(
                        onClick = { /* TODO: Clear cache */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Vider le cache")
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedButton(
                        onClick = { /* TODO: Reset all data */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Réinitialiser toutes les données", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
fun AboutContent() {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Card {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("AppStock", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text("Version 1.0.0")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Application de gestion de stock", style = MaterialTheme.typography.bodyMedium)
                    Text("Développée par LAHMID Lahoucine, en collaboration avec Mohcine IHAYA", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompanyInfoDialog(
    currentCompanyInfo: CompanyInfo?,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, String, String, String, String, String, String, String, String, Double) -> Unit
) {
    var companyName by remember { mutableStateOf(currentCompanyInfo?.companyName ?: "") }
    var address by remember { mutableStateOf(currentCompanyInfo?.address ?: "") }
    var phone by remember { mutableStateOf(currentCompanyInfo?.phone ?: "") }
    var email by remember { mutableStateOf(currentCompanyInfo?.email ?: "") }
    var website by remember { mutableStateOf(currentCompanyInfo?.website ?: "") }
    var ice by remember { mutableStateOf(currentCompanyInfo?.ice ?: "") }
    var ifNumber by remember { mutableStateOf(currentCompanyInfo?.ifNumber ?: "") }
    var patente by remember { mutableStateOf(currentCompanyInfo?.patente ?: "") }
    var rc by remember { mutableStateOf(currentCompanyInfo?.rc ?: "") }
    var bankAccount by remember { mutableStateOf(currentCompanyInfo?.bankAccount ?: "") }
    var logoUri by remember { mutableStateOf(currentCompanyInfo?.logoUri) }
    var invoiceType by remember { mutableStateOf(currentCompanyInfo?.invoiceType ?: "FACTURE") }
    var tvaRate by remember { mutableStateOf(currentCompanyInfo?.tvaRate?.toString() ?: "20.0") }
    var showInvoiceTypeDropdown by remember { mutableStateOf(false) }
    
    // Launcher pour sélectionner une image
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        logoUri = uri?.toString()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Informations de l'entreprise") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                // Informations de base
                Text("Informations générales", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                
                // Section Logo
                Text("Logo de l'entreprise", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .border(
                            width = 2.dp, 
                            color = MaterialTheme.colorScheme.outline,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (logoUri != null) {
                        AsyncImage(
                            model = logoUri,
                            contentDescription = "Logo de l'entreprise",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Store,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                "Appuyez pour sélectionner un logo",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = companyName,
                    onValueChange = { companyName = it },
                    label = { Text("Nom de l'entreprise") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Adresse") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
                
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Téléphone") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = website,
                    onValueChange = { website = it },
                    label = { Text("Site web (optionnel)") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Informations comptables
                Text("Informations comptables", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                
                OutlinedTextField(
                    value = ice,
                    onValueChange = { ice = it },
                    label = { Text("ICE (optionnel)") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = ifNumber,
                    onValueChange = { ifNumber = it },
                    label = { Text("Identifiant Fiscal (optionnel)") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = patente,
                    onValueChange = { patente = it },
                    label = { Text("Patente (optionnel)") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = rc,
                    onValueChange = { rc = it },
                    label = { Text("Registre de Commerce (optionnel)") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = bankAccount,
                    onValueChange = { bankAccount = it },
                    label = { Text("Compte bancaire (optionnel)") },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Configuration de facturation
                Text("Configuration de facturation", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                
                // Sélection du type de document
                ExposedDropdownMenuBox(
                    expanded = showInvoiceTypeDropdown,
                    onExpandedChange = { showInvoiceTypeDropdown = it }
                ) {
                    OutlinedTextField(
                        value = InvoiceType.fromString(invoiceType).displayName,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Type de document") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showInvoiceTypeDropdown) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = showInvoiceTypeDropdown,
                        onDismissRequest = { showInvoiceTypeDropdown = false }
                    ) {
                        InvoiceType.entries.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.displayName) },
                                onClick = {
                                    invoiceType = type.name
                                    showInvoiceTypeDropdown = false
                                }
                            )
                        }
                    }
                }
                
                // Taux de TVA (seulement si facture)
                if (invoiceType == "FACTURE") {
                    OutlinedTextField(
                        value = tvaRate,
                        onValueChange = { tvaRate = it },
                        label = { Text("Taux de TVA (%)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { 
                    val tvaRateValue = tvaRate.toDoubleOrNull() ?: 20.0
                    onSave(companyName, address, phone, email, website, ice, ifNumber, patente, rc, bankAccount, logoUri ?: "", invoiceType, tvaRateValue) 
                }
            ) {
                Text("Enregistrer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}

@Composable
fun BackupDialog(
    onDismiss: () -> Unit,
    onBackup: () -> Unit,
    onRestore: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Sauvegarde et restauration") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Sauvegarder vos données pour éviter toute perte.")
                
                Button(
                    onClick = onBackup,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Backup, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Créer une sauvegarde")
                }
                
                OutlinedButton(
                    onClick = onRestore,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Restaurer depuis une sauvegarde")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Fermer")
            }
        }
    )
}

fun getCategoryTitle(category: String): String = when (category) {
    "invoices" -> "Paramètres des factures"
    "users" -> "Gestion des utilisateurs"
    "printing" -> "Paramètres d'impression"
    "notifications" -> "Notifications"
    "security" -> "Sécurité"
    "appearance" -> "Thème et interface"
    "storage" -> "Stockage et données"
    "about" -> "À propos"
    else -> "Paramètres"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsCategoryCard(
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

