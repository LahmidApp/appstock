package com.example.appstock.utils

import android.content.Context
import android.os.Environment
import com.example.appstock.data.Product
import com.example.appstock.data.Sale
import com.example.appstock.data.Purchase
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object ExcelExporter {

    fun exportProducts(context: Context, products: List<Product>): String? {
        return try {
            val workbook = XSSFWorkbook()
            val sheet = workbook.createSheet("Produits")

            val headerRow = sheet.createRow(0)
            // Assurez-vous que ces en-têtes correspondent bien aux propriétés de Product
            val headers = arrayOf("ID", "Nom", "Description", "Prix de Vente", "Prix de Revient", "Quantité", "Code-barres", "QR Code", "Catégorie", "Fournisseur", "Stock minimum")
            headers.forEachIndexed { index, header ->
                headerRow.createCell(index).setCellValue(header)
            }

            products.forEachIndexed { index, product ->
                val row = sheet.createRow(index + 1)
                row.createCell(0).setCellValue(product.id.toDouble())
                row.createCell(1).setCellValue(product.name)
                row.createCell(2).setCellValue(product.description ?: "")
                row.createCell(3).setCellValue(product.price) // Supposant que product.price est Double
                row.createCell(4).setCellValue(product.costPrice ?: 0.0) // Supposant que product.costPrice est Double?
                row.createCell(5).setCellValue(product.quantity.toDouble())
                row.createCell(6).setCellValue(product.barcode ?: "")
                row.createCell(7).setCellValue(product.qrCode ?: "") // Assurez-vous que qrCode est une prop de Product
                row.createCell(8).setCellValue(product.category ?: "")
                row.createCell(9).setCellValue(product.supplier ?: "")
                row.createCell(10).setCellValue(product.minStockLevel?.toDouble() ?: 0.0)
            }

            for (i in headers.indices) {
                sheet.autoSizeColumn(i)
            }

            val fileName = "produits_${System.currentTimeMillis()}.xlsx"
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
            FileOutputStream(file).use { outputStream ->
                workbook.write(outputStream)
            }
            workbook.close()
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun exportSales(context: Context, sales: List<Sale>): String? {
        return try {
            val workbook = XSSFWorkbook()
            val sheet = workbook.createSheet("Ventes")

            val headerRow = sheet.createRow(0)
            // EN-TÊTES CORRIGÉS pour correspondre à Sale.kt
            val headers = arrayOf("ID Vente", "ID Produit", "Quantité Vendue", "Prix Unitaire", "Prix Total", "ID Client", "Date de la Vente")
            headers.forEachIndexed { index, header ->
                headerRow.createCell(index).setCellValue(header)
            }

            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

            sales.forEachIndexed { index, sale ->
                val row = sheet.createRow(index + 1)
                row.createCell(0).setCellValue(sale.id.toDouble())
                row.createCell(1).setCellValue(sale.productId.toDouble())
                row.createCell(2).setCellValue(sale.quantity.toDouble())
                row.createCell(3).setCellValue(sale.unitPrice)
                row.createCell(4).setCellValue(sale.totalPrice)
                row.createCell(5).setCellValue(sale.customerId?.toDouble() ?: 0.0) // ID Client peut être null
                row.createCell(6).setCellValue(dateFormat.format(Date(sale.dateTimestamp))) // Convertir Long en Date
            }

            for (i in headers.indices) {
                sheet.autoSizeColumn(i)
            }

            val fileName = "ventes_${System.currentTimeMillis()}.xlsx"
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
            FileOutputStream(file).use { outputStream ->
                workbook.write(outputStream)
            }
            workbook.close()
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun exportPurchases(context: Context, purchases: List<Purchase>): String? {
        return try {
            val workbook = XSSFWorkbook()
            val sheet = workbook.createSheet("Achats")

            val headerRow = sheet.createRow(0)
            // EN-TÊTES CORRIGÉS pour correspondre à Purchase.kt
            val headers = arrayOf("ID Achat", "ID Produit", "Quantité Achetée", "Prix Unitaire Achat", "Prix Total Achat", "Fournisseur", "Date de l'Achat")
            headers.forEachIndexed { index, header ->
                headerRow.createCell(index).setCellValue(header)
            }

            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

            purchases.forEachIndexed { index, purchase ->
                val row = sheet.createRow(index + 1)
                row.createCell(0).setCellValue(purchase.id.toDouble())
                row.createCell(1).setCellValue(purchase.productId.toDouble())
                row.createCell(2).setCellValue(purchase.quantity.toDouble())
                row.createCell(3).setCellValue(purchase.unitPrice)
                row.createCell(4).setCellValue(purchase.totalPrice)
                row.createCell(5).setCellValue(purchase.vendor ?: "") // Fournisseur peut être null
                row.createCell(6).setCellValue(dateFormat.format(Date(purchase.dateTimestamp))) // Convertir Long en Date
            }

            for (i in headers.indices) {
                sheet.autoSizeColumn(i)
            }

            val fileName = "achats_${System.currentTimeMillis()}.xlsx"
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
            FileOutputStream(file).use { outputStream ->
                workbook.write(outputStream)
            }
            workbook.close()
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
