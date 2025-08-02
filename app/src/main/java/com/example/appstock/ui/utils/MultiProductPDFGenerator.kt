package com.example.appstock.ui.utils

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.appstock.data.SaleHeader
import com.example.appstock.data.SaleItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Générateur de PDF pour les factures de ventes multi-produits
 */
object MultiProductPDFGenerator {
    
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE)
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale.FRANCE)
    
    /**
     * Génère un PDF de facture pour une vente multi-produits
     */
    suspend fun generateSaleInvoice(
        context: Context,
        saleHeader: SaleHeader,
        saleItems: List<SaleItem>,
        companyInfo: CompanyInfo = CompanyInfo.default()
    ): Result<Uri> = withContext(Dispatchers.IO) {
        try {
            val document = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
            val page = document.startPage(pageInfo)
            val canvas = page.canvas
            
            // Configuration des polices
            val titlePaint = Paint().apply {
                textSize = 20f
                isFakeBoldText = true
                color = android.graphics.Color.BLACK
            }
            
            val headerPaint = Paint().apply {
                textSize = 16f
                isFakeBoldText = true
                color = android.graphics.Color.BLACK
            }
            
            val normalPaint = Paint().apply {
                textSize = 12f
                color = android.graphics.Color.BLACK
            }
            
            val smallPaint = Paint().apply {
                textSize = 10f
                color = android.graphics.Color.GRAY
            }
            
            val boldPaint = Paint().apply {
                textSize = 12f
                isFakeBoldText = true
                color = android.graphics.Color.BLACK
            }
            
            var yPosition = 50f
            val margin = 50f
            val pageWidth = pageInfo.pageWidth.toFloat()
            
            // En-tête de l'entreprise
            canvas.drawText("${companyInfo.name}", margin, yPosition, titlePaint)
            yPosition += 25f
            
            canvas.drawText(companyInfo.address, margin, yPosition, normalPaint)
            yPosition += 15f
            
            canvas.drawText("${companyInfo.phone} - ${companyInfo.email}", margin, yPosition, normalPaint)
            yPosition += 15f
            
            if (companyInfo.siret.isNotEmpty()) {
                canvas.drawText("SIRET: ${companyInfo.siret}", margin, yPosition, smallPaint)
                yPosition += 15f
            }
            
            // Ligne de séparation
            yPosition += 10f
            canvas.drawLine(margin, yPosition, pageWidth - margin, yPosition, normalPaint)
            yPosition += 20f
            
            // Titre de la facture
            val invoiceTitle = "FACTURE N° ${saleHeader.id}"
            canvas.drawText(invoiceTitle, margin, yPosition, headerPaint)
            yPosition += 30f
            
            // Informations client et date
            canvas.drawText("Client ID: ${saleHeader.customerId ?: "Aucun"}", margin, yPosition, normalPaint)
            val dateText = "Date: ${saleHeader.saleDate?.let { dateFormat.format(it) } ?: "N/A"}"
            val dateX = pageWidth - margin - normalPaint.measureText(dateText)
            canvas.drawText(dateText, dateX, yPosition, normalPaint)
            yPosition += 20f
            
            // Statut de la vente
            canvas.drawText("Notes: ${saleHeader.notes ?: "Aucune"}", margin, yPosition, normalPaint)
            yPosition += 30f
            
            // Tableau des produits
            // En-têtes du tableau
            val colPositions = floatArrayOf(margin, margin + 200f, margin + 320f, margin + 380f, margin + 450f)
            val headers = arrayOf("Produit", "Prix unitaire", "Qté", "Total", "")
            
            // Ligne d'en-tête
            canvas.drawLine(margin, yPosition, pageWidth - margin, yPosition, normalPaint)
            yPosition += 15f
            
            headers.forEachIndexed { index, header ->
                if (index < colPositions.size - 1) {
                    canvas.drawText(header, colPositions[index], yPosition, boldPaint)
                }
            }
            yPosition += 10f
            
            canvas.drawLine(margin, yPosition, pageWidth - margin, yPosition, normalPaint)
            yPosition += 20f
            
            // Lignes des produits
            var subtotal = 0.0
            saleItems.forEach { item ->
                // Vérification de l'espace disponible
                if (yPosition > 750f) {
                    // Nouvelle page si nécessaire
                    document.finishPage(page)
                    val newPageInfo = PdfDocument.PageInfo.Builder(595, 842, document.pages.size + 1).create()
                    val newPage = document.startPage(newPageInfo)
                    yPosition = 50f
                }
                
                // Nom du produit (avec gestion du texte long)
                val productName = "Produit #${item.productId}" // Utiliser l'ID en attendant le nom
                val displayName = if (productName.length > 25) {
                    productName.take(22) + "..."
                } else {
                    productName
                }
                canvas.drawText(displayName, colPositions[0], yPosition, normalPaint)
                
                // Prix unitaire
                canvas.drawText(currencyFormat.format(item.unitPrice), colPositions[1], yPosition, normalPaint)
                
                // Quantité
                canvas.drawText(item.quantity.toString(), colPositions[2], yPosition, normalPaint)
                
                // Total ligne
                canvas.drawText(currencyFormat.format(item.totalPrice), colPositions[3], yPosition, normalPaint)
                
                subtotal += item.totalPrice
                yPosition += 20f
            }
            
            // Ligne de séparation avant totaux
            yPosition += 10f
            canvas.drawLine(margin + 300f, yPosition, pageWidth - margin, yPosition, normalPaint)
            yPosition += 20f
            
            // Sous-total
            canvas.drawText("Sous-total:", colPositions[2], yPosition, boldPaint)
            canvas.drawText(currencyFormat.format(subtotal), colPositions[3], yPosition, boldPaint)
            yPosition += 20f
            
            // TVA (si applicable)
            val tvaRate = 0.20 // 20% TVA
            val tvaAmount = subtotal * tvaRate
            canvas.drawText("TVA (20%):", colPositions[2], yPosition, normalPaint)
            canvas.drawText(currencyFormat.format(tvaAmount), colPositions[3], yPosition, normalPaint)
            yPosition += 20f
            
            // Total TTC
            val totalTTC = subtotal + tvaAmount
            canvas.drawText("TOTAL TTC:", colPositions[2], yPosition, boldPaint)
            canvas.drawText(currencyFormat.format(totalTTC), colPositions[3], yPosition, boldPaint)
            yPosition += 30f
            
            // Informations de paiement
            yPosition += 20f
            canvas.drawText("Informations de paiement:", margin, yPosition, boldPaint)
            yPosition += 20f
            
            canvas.drawText("• Paiement à réception de facture", margin + 20f, yPosition, normalPaint)
            yPosition += 15f
            canvas.drawText("• Virement bancaire ou espèces", margin + 20f, yPosition, normalPaint)
            yPosition += 15f
            canvas.drawText("• Aucun escompte pour paiement anticipé", margin + 20f, yPosition, normalPaint)
            
            // Pied de page
            yPosition = 800f
            canvas.drawText("Merci pour votre confiance !", margin, yPosition, smallPaint)
            val footerText = "Document généré le ${SimpleDateFormat("dd/MM/yyyy à HH:mm", Locale.FRANCE).format(Date())}"
            val footerX = pageWidth - margin - smallPaint.measureText(footerText)
            canvas.drawText(footerText, footerX, yPosition, smallPaint)
            
            document.finishPage(page)
            
            // Sauvegarde du PDF
            val fileName = "facture_${saleHeader.id}_${System.currentTimeMillis()}.pdf"
            val file = File(context.getExternalFilesDir(null), fileName)
            
            FileOutputStream(file).use { outputStream ->
                document.writeTo(outputStream)
            }
            
            document.close()
            
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            
            Result.success(uri)
            
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Génère un PDF récapitulatif de plusieurs ventes
     */
    suspend fun generateSalesSummaryPDF(
        context: Context,
        salesWithItems: List<Pair<SaleHeader, List<SaleItem>>>,
        title: String = "Récapitulatif des Ventes",
        companyInfo: CompanyInfo = CompanyInfo.default()
    ): Result<Uri> = withContext(Dispatchers.IO) {
        try {
            val document = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = document.startPage(pageInfo)
            val canvas = page.canvas
            
            val titlePaint = Paint().apply {
                textSize = 18f
                isFakeBoldText = true
                color = android.graphics.Color.BLACK
            }
            
            val normalPaint = Paint().apply {
                textSize = 12f
                color = android.graphics.Color.BLACK
            }
            
            val boldPaint = Paint().apply {
                textSize = 12f
                isFakeBoldText = true
                color = android.graphics.Color.BLACK
            }
            
            var yPosition = 50f
            val margin = 50f
            val pageWidth = pageInfo.pageWidth.toFloat()
            
            // Titre
            canvas.drawText(title, margin, yPosition, titlePaint)
            yPosition += 30f
            
            canvas.drawText("Période: ${SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE).format(Date())}", margin, yPosition, normalPaint)
            yPosition += 30f
            
            // Statistiques générales
            val totalSales = salesWithItems.size
            val totalAmount = salesWithItems.sumOf { it.first.totalAmount }
            val totalItems = salesWithItems.sumOf { it.second.size }
            
            canvas.drawText("Nombre de ventes: $totalSales", margin, yPosition, boldPaint)
            yPosition += 20f
            canvas.drawText("Total articles vendus: $totalItems", margin, yPosition, boldPaint)
            yPosition += 20f
            canvas.drawText("Chiffre d'affaires: ${currencyFormat.format(totalAmount)}", margin, yPosition, boldPaint)
            yPosition += 30f
            
            // Liste des ventes
            canvas.drawText("Détail des ventes:", margin, yPosition, boldPaint)
            yPosition += 20f
            
            salesWithItems.forEach { (saleHeader, saleItems) ->
                if (yPosition > 750f) {
                    document.finishPage(page)
                    val newPageInfo = PdfDocument.PageInfo.Builder(595, 842, document.pages.size + 1).create()
                    val newPage = document.startPage(newPageInfo)
                    yPosition = 50f
                }
                
                val saleText = "Vente #${saleHeader.id} - Client ID: ${saleHeader.customerId ?: "Aucun"} - ${currencyFormat.format(saleHeader.totalAmount)}"
                canvas.drawText(saleText, margin, yPosition, normalPaint)
                yPosition += 15f
                
                val dateText = "Date: ${saleHeader.saleDate?.let { dateFormat.format(it) } ?: "N/A"} - ${saleItems.size} article(s)"
                canvas.drawText(dateText, margin + 20f, yPosition, normalPaint)
                yPosition += 20f
            }
            
            document.finishPage(page)
            
            val fileName = "recap_ventes_${System.currentTimeMillis()}.pdf"
            val file = File(context.getExternalFilesDir(null), fileName)
            
            FileOutputStream(file).use { outputStream ->
                document.writeTo(outputStream)
            }
            
            document.close()
            
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            
            Result.success(uri)
            
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

/**
 * Informations de l'entreprise pour les PDF
 */
data class CompanyInfo(
    val name: String,
    val address: String,
    val phone: String,
    val email: String,
    val siret: String = ""
) {
    companion object {
        fun default() = CompanyInfo(
            name = "AppStock Entreprise",
            address = "123 Rue du Commerce, 75001 Paris",
            phone = "01 23 45 67 89",
            email = "contact@appstock.com",
            siret = "12345678901234"
        )
    }
}
