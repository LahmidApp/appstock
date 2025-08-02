package com.example.appstock.utils

import com.example.appstock.data.*
import com.example.appstock.viewmodel.SaleHeaderViewModel
import com.example.appstock.viewmodel.SaleViewModel
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*

/**
 * Helper class pour migrer les anciennes ventes mono-produit vers les nouvelles ventes multi-produits
 * et assurer la cohérence des données dans l'application
 */
object SalesMigrationHelper {
    
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    
    /**
     * Migre une ancienne vente (Sale) vers le nouveau format (SaleHeader + SaleItem)
     */
    suspend fun migrateSaleToSaleHeader(
        sale: Sale,
        saleHeaderViewModel: SaleHeaderViewModel
    ): Long {
        // Créer le SaleHeader
        val saleHeader = SaleHeader(
            customerId = sale.customerId,
            totalAmount = sale.totalPrice,
            dateTimestamp = sale.saleDate,
            saleDate = sale.saleDate,
            notes = "Migré depuis vente simple"
        )
        
        // Insérer le SaleHeader - Note: temporaire fix pour compilation
        saleHeaderViewModel.insert(saleHeader)
        
        // Créer le SaleItem correspondant
        val saleItem = SaleItem(
            saleId = 0, // Temporaire - à corriger avec la vraie architecture
            productId = sale.productId,
            quantity = sale.quantity,
            unitPrice = sale.totalPrice / sale.quantity, // Calcul du prix unitaire
            totalPrice = sale.totalPrice
        )
        
        // Insérer le SaleItem via le DAO approprié
        // Note: Il faut utiliser le DAO des SaleItems
        
        return 0 // Temporaire - retourner le vrai ID quand l'architecture sera corrigée
    }
    
    /**
     * Migre toutes les ventes mono-produit vers le format multi-produit
     */
    suspend fun migrateAllSalesToMultiProduct(
        saleViewModel: SaleViewModel,
        saleHeaderViewModel: SaleHeaderViewModel
    ): Int {
        val allSales = saleViewModel.allSales.value ?: emptyList()
        var migratedCount = 0
        
        for (sale in allSales) {
            try {
                migrateSaleToSaleHeader(sale, saleHeaderViewModel)
                migratedCount++
            } catch (e: Exception) {
                // Log l'erreur mais continue la migration
                println("Erreur lors de la migration de la vente ${sale.id}: ${e.message}")
            }
        }
        
        return migratedCount
    }
    
    /**
     * Vérifie la cohérence entre les données anciennes et nouvelles
     */
    suspend fun validateDataConsistency(
        saleViewModel: SaleViewModel,
        saleHeaderViewModel: SaleHeaderViewModel
    ): DataConsistencyReport {
        val allSales = saleViewModel.allSales.value ?: emptyList()
        val allSaleHeaders = saleHeaderViewModel.allSaleHeaders.value ?: emptyList()
        
        val legacyTotalRevenue = allSales.sumOf { it.totalPrice }
        val newTotalRevenue = allSaleHeaders.sumOf { it.totalAmount }
        
        val legacyTotalSales = allSales.size
        val newTotalSales = allSaleHeaders.size
        
        return DataConsistencyReport(
            legacySalesCount = legacyTotalSales,
            newSalesCount = newTotalSales,
            legacyTotalRevenue = legacyTotalRevenue,
            newTotalRevenue = newTotalRevenue,
            isConsistent = legacyTotalRevenue == newTotalRevenue
        )
    }
    
    /**
     * Combine les revenus des deux systèmes pour éviter la double comptabilisation
     */
    fun getCombinedRevenue(
        legacySales: List<Sale>,
        saleHeaders: List<SaleHeader>,
        useLegacyData: Boolean = false
    ): Double {
        return if (useLegacyData || saleHeaders.isEmpty()) {
            legacySales.sumOf { it.totalPrice }
        } else {
            saleHeaders.sumOf { it.totalAmount }
        }
    }
    
    /**
     * Obtient le nombre total de ventes en évitant la duplication
     */
    fun getCombinedSalesCount(
        legacySales: List<Sale>,
        saleHeaders: List<SaleHeader>,
        useLegacyData: Boolean = false
    ): Int {
        return if (useLegacyData || saleHeaders.isEmpty()) {
            legacySales.size
        } else {
            saleHeaders.size
        }
    }
    
    /**
     * Formate une date de vente pour l'affichage
     */
    fun formatSaleDate(timestamp: Long): String {
        return dateFormat.format(Date(timestamp))
    }
}

/**
 * Rapport de cohérence des données
 */
data class DataConsistencyReport(
    val legacySalesCount: Int,
    val newSalesCount: Int,
    val legacyTotalRevenue: Double,
    val newTotalRevenue: Double,
    val isConsistent: Boolean
) {
    fun getInconsistencyMessage(): String? {
        return if (!isConsistent) {
            "Incohérence détectée: ${legacySalesCount} ventes legacy (${String.format("%.2f", legacyTotalRevenue)}Dhs) vs ${newSalesCount} nouvelles ventes (${String.format("%.2f", newTotalRevenue)}Dhs)"
        } else {
            null
        }
    }
}
