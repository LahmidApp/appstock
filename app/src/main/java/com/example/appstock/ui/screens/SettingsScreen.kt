package com.example.appstock.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Info

/**
 * Settings screen for app configuration
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
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
        
        // Settings categories
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                SettingsCategoryCard(
                    title = "Informations de la librairie",
                    description = "Nom, logo, adresse et coordonnées",
                    icon = Icons.Default.Store,
                    onClick = { /* TODO: Handle library info */ }
                )
            }
            
            item {
                SettingsCategoryCard(
                    title = "Configuration fiscale",
                    description = "Taux de TVA et paramètres de facturation",
                    icon = Icons.Default.Receipt,
                    onClick = { /* TODO: Handle tax settings */ }
                )
            }
            
            item {
                SettingsCategoryCard(
                    title = "Gestion des utilisateurs",
                    description = "Comptes et permissions d'accès",
                    icon = Icons.Default.People,
                    onClick = { /* TODO: Handle user management */ }
                )
            }
            
            item {
                SettingsCategoryCard(
                    title = "Sauvegarde et synchronisation",
                    description = "Backup automatique et cloud",
                    icon = Icons.Default.CloudSync,
                    onClick = { /* TODO: Handle backup settings */ }
                )
            }
            
            item {
                SettingsCategoryCard(
                    title = "Notifications",
                    description = "Alertes de stock et rappels",
                    icon = Icons.Filled.Notifications,
                    onClick = { /* TODO: Handle notification settings */ }
                )
            }
            
            item {
                SettingsCategoryCard(
                    title = "Imprimantes et périphériques",
                    description = "Configuration des imprimantes",
                    icon = Icons.Default.Print,
                    onClick = { /* TODO: Handle printer settings */ }
                )
            }
            
            item {
                SettingsCategoryCard(
                    title = "Sécurité",
                    description = "Mots de passe et authentification",
                    icon = Icons.Default.Security,
                    onClick = { /* TODO: Handle security settings */ }
                )
            }
            
            item {
                SettingsCategoryCard(
                    title = "À propos",
                    description = "Version de l'app et informations",
                    icon = Icons.Filled.Info,
                    onClick = { /* TODO: Handle about */ }
                )
            }
        }
    }
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

