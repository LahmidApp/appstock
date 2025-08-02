package com.example.appstock.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Composant de pied de page pour toute l'application
 * Affiche "Application pour IHAYA Mohcine" sur un fond bleu ciel
 */
@Composable
fun AppFooter(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(Color(0xFF87CEEB)), // Bleu ciel
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Application pour IHAYA Mohcine",
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Wrapper qui ajoute automatiquement le footer à une screen
 */
@Composable
fun ScreenWithFooter(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        // Contenu principal
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 48.dp) // Espace pour le footer
        ) {
            content()
        }
        
        // Footer fixe en bas
        AppFooter(
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
