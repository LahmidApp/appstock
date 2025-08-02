package com.example.appstock.ui.utils

import android.content.Context
import com.example.appstock.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Helper temporaire pour avoir des noms de produits dans les ventes
 */
data class SaleItemWithProduct(
    val saleItem: SaleItem,
    val productName: String
)

data class SaleHeaderWithItems(
    val saleHeader: SaleHeader,
    val items: List<SaleItemWithProduct>
)

/**
 * Helper pour récupérer les ventes avec les noms des produits
 */
suspend fun getSalesWithProductNames(
    salesWithItems: List<Pair<SaleHeader, List<SaleItem>>>,
    productRepository: Any // À typer correctement quand le repository sera disponible
): List<SaleHeaderWithItems> = withContext(Dispatchers.IO) {
    salesWithItems.map { (saleHeader, saleItems) ->
        val itemsWithProducts = saleItems.map { saleItem ->
            SaleItemWithProduct(
                saleItem = saleItem,
                productName = "Produit #${saleItem.productId}" // Temporaire
            )
        }
        SaleHeaderWithItems(
            saleHeader = saleHeader,
            items = itemsWithProducts
        )
    }
}
