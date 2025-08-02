package com.example.appstock.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.appstock.data.LibrarySettings
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaxSettingsScreen(
    initialSettings: LibrarySettings = LibrarySettings(name = ""),
    onSave: (BigDecimal) -> Unit = {}
) {
    var taxRate by remember { mutableStateOf(initialSettings.taxRate.toString()) }
    var currency by remember { mutableStateOf(initialSettings.currency) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Configuration fiscale", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = taxRate,
            onValueChange = { taxRate = it },
            label = { Text("Taux de TVA (%)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = currency,
            onValueChange = { currency = it },
            label = { Text("Devise") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Button(
            onClick = {
                val rate = taxRate.toBigDecimalOrNull() ?: BigDecimal.ZERO
                onSave(rate)
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Icon(Icons.Default.Save, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Enregistrer")
        }
    }
}
