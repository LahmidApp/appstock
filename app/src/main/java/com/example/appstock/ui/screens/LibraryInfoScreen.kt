package com.example.appstock.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.appstock.data.LibrarySettings
import android.graphics.BitmapFactory
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryInfoScreen(
    initialSettings: LibrarySettings = LibrarySettings(
        name = "",
        logoPath = null,
        address = null,
        phone = null,
        email = null
    ),
    onSave: (LibrarySettings) -> Unit = {},
    onPickLogo: (() -> String?)? = null
) {
    var name by remember { mutableStateOf(initialSettings.name) }
    var address by remember { mutableStateOf(initialSettings.address ?: "") }
    var phone by remember { mutableStateOf(initialSettings.phone ?: "") }
    var email by remember { mutableStateOf(initialSettings.email ?: "") }
    var logoPath by remember { mutableStateOf(initialSettings.logoPath) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Informations de la librairie", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nom de la librairie") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Adresse") },
            modifier = Modifier.fillMaxWidth()
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
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (logoPath != null && File(logoPath!!).exists()) {
                val bitmap = BitmapFactory.decodeFile(logoPath)
                bitmap?.let {
                    Image(it.asImageBitmap(), contentDescription = "Logo", modifier = Modifier.size(64.dp).padding(end = 16.dp))
                }
            }
            Button(onClick = {
                val picked = onPickLogo?.invoke()
                if (picked != null) logoPath = picked
            }) {
                Text("Choisir un logo")
            }
        }
        Button(
            onClick = {
                onSave(
                    LibrarySettings(
                        name = name,
                        logoPath = logoPath,
                        address = address,
                        phone = phone,
                        email = email
                    )
                )
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Icon(Icons.Default.Save, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Enregistrer")
        }
    }
}
