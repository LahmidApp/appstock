package com.example.appstock.ui.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.appstock.data.Customer
import com.example.appstock.data.Product
import com.example.appstock.data.Quote
import com.example.appstock.data.SaleHeader
import com.example.appstock.data.SaleItem
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

/**
 * Utilitaire pour l'export CSV basé sur les vrais modèles de données
 */
object CSVExportUtility {
    
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    
    /**
     * Export des produits en CSV
     * Modèle Product: id, name, price, costPrice, stock, barcode, types, supplier, minStockLevel, qrCode, description, photoUri
     */
    fun exportProducts(context: Context, products: List<Product>): Uri? {
        return try {
            val fileName = "produits_${System.currentTimeMillis()}.csv"
            val file = File(context.getExternalFilesDir(null), fileName)
            
            FileWriter(file).use { writer ->
                // En-tête CSV
                writer.append("ID,Nom,Prix,Prix d'achat,Stock,Code-barres,Catégories,Fournisseur,Stock minimum,QR Code,Description,Photo URI\n")
                
                // Données
                products.forEach { product ->
                    writer.append(escapeCSV(product.id.toString()))
                    writer.append(",")
                    writer.append(escapeCSV(product.name))
                    writer.append(",")
                    writer.append(escapeCSV(product.price.toString()))
                    writer.append(",")
                    writer.append(escapeCSV(product.costPrice.toString()))
                    writer.append(",")
                    writer.append(escapeCSV(product.stock.toString()))
                    writer.append(",")
                    writer.append(escapeCSV(product.barcode ?: ""))
                    writer.append(",")
                    writer.append(escapeCSV(product.types.joinToString(";")))
                    writer.append(",")
                    writer.append(escapeCSV(product.supplier ?: ""))
                    writer.append(",")
                    writer.append(escapeCSV(product.minStockLevel?.toString() ?: ""))
                    writer.append(",")
                    writer.append(escapeCSV(product.qrCode ?: ""))
                    writer.append(",")
                    writer.append(escapeCSV(product.description ?: ""))
                    writer.append(",")
                    writer.append(escapeCSV(product.photoUri ?: ""))
                    writer.append("\n")
                }
            }
            
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Export des clients en CSV
     * Modèle Customer: id, name, phoneNumber, email, address, createdAt
     */
    fun exportCustomers(context: Context, customers: List<Customer>): Uri? {
        return try {
            val fileName = "clients_${System.currentTimeMillis()}.csv"
            val file = File(context.getExternalFilesDir(null), fileName)
            
            FileWriter(file).use { writer ->
                // En-tête CSV
                writer.append("ID,Nom,Téléphone,Email,Adresse,Date de création\n")
                
                // Données
                customers.forEach { customer ->
                    writer.append(escapeCSV(customer.id.toString()))
                    writer.append(",")
                    writer.append(escapeCSV(customer.name))
                    writer.append(",")
                    writer.append(escapeCSV(customer.phoneNumber ?: ""))
                    writer.append(",")
                    writer.append(escapeCSV(customer.email ?: ""))
                    writer.append(",")
                    writer.append(escapeCSV(customer.address ?: ""))
                    writer.append(",")
                    writer.append(escapeCSV(dateFormat.format(Date(customer.createdAt))))
                    writer.append("\n")
                }
            }
            
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Export des devis en CSV
     * Modèle Quote: id, customerId, productId, quantity, unitPrice, totalAmount, description, dateTimestamp, status
     */
    fun exportQuotes(context: Context, quotes: List<Quote>): Uri? {
        return try {
            val fileName = "devis_${System.currentTimeMillis()}.csv"
            val file = File(context.getExternalFilesDir(null), fileName)
            
            FileWriter(file).use { writer ->
                // En-tête CSV
                writer.append("ID,ID Client,ID Produit,Quantité,Prix unitaire,Montant total,Description,Date,Statut\n")
                
                // Données
                quotes.forEach { quote ->
                    writer.append(escapeCSV(quote.id.toString()))
                    writer.append(",")
                    writer.append(escapeCSV(quote.customerId.toString()))
                    writer.append(",")
                    writer.append(escapeCSV(quote.productId.toString()))
                    writer.append(",")
                    writer.append(escapeCSV(quote.quantity.toString()))
                    writer.append(",")
                    writer.append(escapeCSV(quote.unitPrice.toString()))
                    writer.append(",")
                    writer.append(escapeCSV(quote.totalAmount.toString()))
                    writer.append(",")
                    writer.append(escapeCSV(quote.description))
                    writer.append(",")
                    writer.append(escapeCSV(dateFormat.format(Date(quote.dateTimestamp))))
                    writer.append(",")
                    writer.append(escapeCSV(quote.status))
                    writer.append("\n")
                }
            }
            
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Export des en-têtes de vente en CSV
     * Modèle SaleHeader: id, customerId, totalAmount, dateTimestamp, saleDate, notes
     */
    fun exportSaleHeaders(context: Context, saleHeaders: List<SaleHeader>): Uri? {
        return try {
            val fileName = "ventes_${System.currentTimeMillis()}.csv"
            val file = File(context.getExternalFilesDir(null), fileName)
            
            FileWriter(file).use { writer ->
                // En-tête CSV
                writer.append("ID,ID Client,Montant total,Date,Notes\n")
                
                // Données
                saleHeaders.forEach { saleHeader ->
                    writer.append(escapeCSV(saleHeader.id.toString()))
                    writer.append(",")
                    writer.append(escapeCSV(saleHeader.customerId?.toString() ?: ""))
                    writer.append(",")
                    writer.append(escapeCSV(saleHeader.totalAmount.toString()))
                    writer.append(",")
                    writer.append(escapeCSV(dateFormat.format(Date(saleHeader.dateTimestamp))))
                    writer.append(",")
                    writer.append(escapeCSV(saleHeader.notes ?: ""))
                    writer.append("\n")
                }
            }
            
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Export des éléments de vente en CSV  
     * Modèle SaleItem: id, saleId, productId, quantity, unitPrice, totalPrice
     */
    fun exportSaleItems(context: Context, saleItems: List<SaleItem>): Uri? {
        return try {
            val fileName = "elements_ventes_${System.currentTimeMillis()}.csv"
            val file = File(context.getExternalFilesDir(null), fileName)
            
            FileWriter(file).use { writer ->
                // En-tête CSV
                writer.append("ID,ID Vente,ID Produit,Quantité,Prix unitaire,Prix total\n")
                
                // Données
                saleItems.forEach { saleItem ->
                    writer.append(escapeCSV(saleItem.id.toString()))
                    writer.append(",")
                    writer.append(escapeCSV(saleItem.saleId.toString()))
                    writer.append(",")
                    writer.append(escapeCSV(saleItem.productId.toString()))
                    writer.append(",")
                    writer.append(escapeCSV(saleItem.quantity.toString()))
                    writer.append(",")
                    writer.append(escapeCSV(saleItem.unitPrice.toString()))
                    writer.append(",")
                    writer.append(escapeCSV(saleItem.totalPrice.toString()))
                    writer.append("\n")
                }
            }
            
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Partage un fichier CSV
     */
    fun shareCSVFile(context: Context, uri: Uri, fileName: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "Export CSV - $fileName")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Partager le fichier CSV"))
    }
    
    /**
     * Échappe les caractères spéciaux CSV
     */
    private fun escapeCSV(text: String): String {
        return if (text.contains(",") || text.contains("\"") || text.contains("\n")) {
            "\"${text.replace("\"", "\"\"")}\"" 
        } else {
            text
        }
    }
}
