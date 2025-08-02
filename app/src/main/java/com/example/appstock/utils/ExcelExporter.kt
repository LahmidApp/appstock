package com.example.appstock.utils

import android.content.Context
import android.os.Environment
import com.example.appstock.data.Product
import com.example.appstock.data.Sale
import com.example.appstock.data.Purchase
import com.example.appstock.data.Customer
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.*

object ExcelExporter {

    /**
     * Export en CSV pour compatibilité Android complète
     */
    fun exportCustomersToCSV(context: Context, customers: List<Customer>): String? {
        return try {
            val fileName = "clients_${System.currentTimeMillis()}.csv"
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            
            FileOutputStream(file).use { fos ->
                OutputStreamWriter(fos, "UTF-8").use { writer ->
                    // En-têtes CSV
                    writer.write("ID,Nom,Email,Téléphone,Adresse,Date de création\n")
                    
                    // Données
                    customers.forEach { customer ->
                        val line = listOf(
                            customer.id.toString(),
                            "\"${customer.name}\"",
                            "\"${customer.email ?: ""}\"",
                            "\"${customer.phoneNumber ?: ""}\"",
                            "\"${customer.address ?: ""}\"",
                            "\"${customer.createdAt?.let { dateFormat.format(Date(it)) } ?: ""}\""
                        ).joinToString(",")
                        writer.write("$line\n")
                    }
                }
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Export produits en CSV
     */
    fun exportProductsToCSV(context: Context, products: List<Product>): String? {
        return try {
            val fileName = "produits_${System.currentTimeMillis()}.csv"
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
            
            FileOutputStream(file).use { fos ->
                OutputStreamWriter(fos, "UTF-8").use { writer ->
                    // En-têtes CSV
                    writer.write("ID,Nom,Description,Prix de Vente,Prix de Revient,Quantité,Code-barres,QR Code,Catégorie,Fournisseur,Stock minimum\n")
                    
                    // Données
                    products.forEach { product ->
                        val line = listOf(
                            product.id.toString(),
                            "\"${product.name}\"",
                            "\"${product.description ?: ""}\"",
                            product.price.toString(),
                            product.costPrice.toString(),
                            product.stock.toString(),
                            "\"${product.barcode}\"",
                            "\"${product.qrCode}\"",
                            "\"${product.types.joinToString(", ")}\"",
                            "\"${product.supplier ?: ""}\"",
                            "${product.minStockLevel ?: 0}"
                        ).joinToString(",")
                        writer.write("$line\n")
                    }
                }
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Export ventes en CSV
     */
    fun exportSalesToCSV(context: Context, sales: List<Sale>): String? {
        return try {
            val fileName = "ventes_${System.currentTimeMillis()}.csv"
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            
            FileOutputStream(file).use { fos ->
                OutputStreamWriter(fos, "UTF-8").use { writer ->
                    // En-têtes CSV
                    writer.write("ID,Client ID,Produit ID,Quantité,Prix unitaire,Prix total,Date de vente\n")
                    
                    // Données
                    sales.forEach { sale ->
                        val line = listOf(
                            sale.id.toString(),
                            sale.customerId?.toString() ?: "",
                            sale.productId.toString(),
                            sale.quantity.toString(),
                            sale.unitPrice.toString(),
                            sale.totalPrice.toString(),
                            "\"${dateFormat.format(Date(sale.saleDate))}\""
                        ).joinToString(",")
                        writer.write("$line\n")
                    }
                }
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Export achats en CSV
     */
    fun exportPurchasesToCSV(context: Context, purchases: List<Purchase>): String? {
        return try {
            val fileName = "achats_${System.currentTimeMillis()}.csv"
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            
            FileOutputStream(file).use { fos ->
                OutputStreamWriter(fos, "UTF-8").use { writer ->
                    // En-têtes CSV
                    writer.write("ID,Produit ID,Quantité,Prix unitaire,Prix total,Date d'achat,Fournisseur\n")
                    
                    // Données
                    purchases.forEach { purchase ->
                        val line = listOf(
                            purchase.id.toString(),
                            purchase.productId.toString(),
                            purchase.quantity.toString(),
                            purchase.unitPrice.toString(),
                            purchase.totalPrice.toString(),
                            "\"${dateFormat.format(Date(purchase.dateTimestamp))}\"",
                            "\"${purchase.vendor ?: ""}\""
                        ).joinToString(",")
                        writer.write("$line\n")
                    }
                }
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Méthodes de compatibilité pour l'ancien code (fallback vers CSV)
     */
    fun exportCustomers(context: Context, customers: List<Customer>): String? = exportCustomersToCSV(context, customers)
    fun exportProducts(context: Context, products: List<Product>): String? = exportProductsToCSV(context, products)
    fun exportSales(context: Context, sales: List<Sale>): String? = exportSalesToCSV(context, sales)
    fun exportPurchases(context: Context, purchases: List<Purchase>): String? = exportPurchasesToCSV(context, purchases)
}
