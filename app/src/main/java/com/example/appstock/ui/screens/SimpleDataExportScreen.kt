package com.example.appstock.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.appstock.ui.utils.*
import kotlinx.coroutines.launch

/**
 * Version simplifiée de l'écran d'export CSV (temporaire)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleDataExportScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    var exportMessage by remember { mutableStateOf<String?>(null) }
    var isExporting by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Export de Données",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Card {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Export CSV",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = {
                        scope.launch {
                            isExporting = true
                            try {
                                // Export simple simulé
                                kotlinx.coroutines.delay(1000)
                                exportMessage = "Export simulé réussi !"
                            } catch (e: Exception) {
                                exportMessage = "Erreur: ${e.message}"
                            } finally {
                                isExporting = false
                            }
                        }
                    },
                    enabled = !isExporting,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isExporting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Export en cours...")
                    } else {
                        Icon(Icons.Default.Download, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Simuler Export CSV")
                    }
                }
            }
        }
        
        exportMessage?.let { message ->
            Spacer(modifier = Modifier.height(16.dp))
            
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (message.startsWith("Erreur")) {
                        MaterialTheme.colorScheme.errorContainer
                    } else {
                        MaterialTheme.colorScheme.primaryContainer
                    }
                )
            ) {
                Text(
                    text = message,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}
