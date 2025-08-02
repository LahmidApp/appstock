package com.example.appstock.utils

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PageRange
import com.example.appstock.data.Invoice
import com.example.appstock.data.Customer
import com.example.appstock.data.Sale
import com.example.appstock.data.CompanyInfo
import com.example.appstock.data.InvoiceItem
import com.example.appstock.data.Product
import com.example.appstock.data.InvoiceType
import android.graphics.Typeface
import android.graphics.Rect
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.net.Uri
import java.text.SimpleDateFormat
import java.util.*
import java.io.FileOutputStream

class InvoicePrintAdapter(
    private val context: android.content.Context,
    private val invoice: Invoice,
    private val customer: Customer?,
    private val sale: Sale?,
    private val companyInfo: CompanyInfo? = null,
    private val invoiceItems: List<InvoiceItem> = emptyList(),
    private val products: List<Product> = emptyList()
) : PrintDocumentAdapter() {
    override fun onLayout(
        oldAttributes: PrintAttributes?,
        newAttributes: PrintAttributes?,
        cancellationSignal: CancellationSignal?,
        callback: LayoutResultCallback?,
        extras: android.os.Bundle?
    ) {
        if (cancellationSignal?.isCanceled == true) {
            callback?.onLayoutCancelled()
            return
        }
        // Déterminer le type de document et le nom du fichier
        val invoiceType = InvoiceType.fromString(companyInfo?.invoiceType ?: "FACTURE")
        val fileName = "${invoiceType.fileName}_${invoice.id}.pdf"
        
        val info = PrintDocumentInfo.Builder(fileName)
            .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
            .build()
        callback?.onLayoutFinished(info, true)
    }

    override fun onWrite(
        pages: Array<out PageRange>,
        destination: ParcelFileDescriptor,
        cancellationSignal: CancellationSignal,
        callback: WriteResultCallback
    ) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas
        
        try {
            drawInvoiceContent(canvas)
            pdfDocument.finishPage(page)
            
            val out = FileOutputStream(destination?.fileDescriptor)
            pdfDocument.writeTo(out)
            callback?.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
        } catch (e: Exception) {
            callback?.onWriteFailed(e.message)
        } finally {
            pdfDocument.close()
        }
    }
    
    private fun drawInvoiceContent(canvas: Canvas) {
        val pageWidth = 595f
        val pageHeight = 842f
        val margin = 40f
        var currentY = margin
        
        // Configuration des styles de texte
        val titlePaint = Paint().apply {
            textSize = 24f
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }
        
        val headerPaint = Paint().apply {
            textSize = 16f
            typeface = Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }
        
        val normalPaint = Paint().apply {
            textSize = 12f
            isAntiAlias = true
        }
        
        val smallPaint = Paint().apply {
            textSize = 10f
            isAntiAlias = true
        }
        
        val linePaint = Paint().apply {
            strokeWidth = 1f
            style = Paint.Style.STROKE
        }
        
        // === EN-TÊTE ENTREPRISE ===
        currentY = drawCompanyHeader(canvas, titlePaint, headerPaint, normalPaint, margin, currentY, pageWidth)
        
        currentY += 30f
        
        // === TITRE DU DOCUMENT ===
        val documentType = InvoiceType.fromString(companyInfo?.invoiceType ?: "FACTURE")
        val titleText = documentType.displayName.uppercase()
        val titleWidth = titlePaint.measureText(titleText)
        canvas.drawText(titleText, (pageWidth - titleWidth) / 2f, currentY, titlePaint)
        currentY += 30f
        
        // === INFORMATIONS FACTURE ET CLIENT ===
        currentY = drawInvoiceAndClientInfo(canvas, headerPaint, normalPaint, margin, currentY, pageWidth)
        
        currentY += 30f
        
        // === TABLEAU DES ARTICLES ===
        currentY = drawItemsTable(canvas, headerPaint, normalPaint, linePaint, margin, currentY, pageWidth)
        
        currentY += 30f
        
        // === TOTAUX ===
        currentY = drawTotals(canvas, headerPaint, normalPaint, margin, currentY, pageWidth)
        
        currentY += 40f
        
        // === INFORMATIONS COMPLÉMENTAIRES ===
        drawFooterInfo(canvas, smallPaint, normalPaint, margin, currentY, pageWidth, pageHeight)
    }
    
    private fun drawCompanyHeader(canvas: Canvas, titlePaint: Paint, headerPaint: Paint, normalPaint: Paint, margin: Float, startY: Float, pageWidth: Float): Float {
        var y = startY
        
        companyInfo?.let { company ->
            // Logo de l'entreprise (si disponible)
            company.logoUri?.let { logoUri ->
                try {
                    val inputStream = context.contentResolver.openInputStream(Uri.parse(logoUri))
                    inputStream?.let { stream ->
                        val logoBitmap = BitmapFactory.decodeStream(stream)
                        stream.close()
                        
                        if (logoBitmap != null && !logoBitmap.isRecycled) {
                            val logoSize = 80f
                            val logoX = pageWidth - margin - logoSize
                            canvas.drawBitmap(
                                logoBitmap,
                                null,
                                Rect(logoX.toInt(), (y - 10).toInt(), (logoX + logoSize).toInt(), (y + logoSize - 10).toInt()),
                                null
                            )
                            logoBitmap.recycle()
                        }
                    }
                } catch (e: Exception) {
                    // Log l'erreur pour déboguer
                    android.util.Log.e("InvoicePrintAdapter", "Erreur lors du chargement du logo: ${e.message}")
                }
            }
            
            // Nom de l'entreprise
            canvas.drawText(company.companyName, margin, y, titlePaint)
            y += 25f
            
            // Adresse
            canvas.drawText(company.address, margin, y, normalPaint)
            y += 15f
            
            // Téléphone et Email
            canvas.drawText("Tél: ${company.phone}", margin, y, normalPaint)
            y += 15f
            canvas.drawText("Email: ${company.email}", margin, y, normalPaint)
            y += 15f
            
            // Site web (si disponible)
            company.website?.let { website ->
                canvas.drawText("Web: $website", margin, y, normalPaint)
                y += 15f
            }
            
            y += 10f
            
            // Informations comptables
            company.ice?.let { ice ->
                canvas.drawText("ICE: $ice", margin, y, normalPaint)
                y += 12f
            }
            
            company.ifNumber?.let { ifNum ->
                canvas.drawText("IF: $ifNum", margin, y, normalPaint)
                y += 12f
            }
            
            company.rc?.let { rc ->
                canvas.drawText("RC: $rc", margin, y, normalPaint)
                y += 12f
            }
        } ?: run {
            // Fallback si pas d'infos entreprise
            canvas.drawText("Votre Entreprise", margin, y, titlePaint)
            y += 25f
        }
        
        return y
    }
    
    private fun drawInvoiceAndClientInfo(canvas: Canvas, headerPaint: Paint, normalPaint: Paint, margin: Float, startY: Float, pageWidth: Float): Float {
        var y = startY
        
        // Colonne gauche - Informations Client
        canvas.drawText("FACTURÉ À:", margin, y, headerPaint)
        y += 20f
        
        customer?.let { client ->
            canvas.drawText(client.name, margin, y, normalPaint)
            y += 15f
            client.email?.let { email ->
                canvas.drawText(email, margin, y, normalPaint)
                y += 15f
            }
            client.phoneNumber?.let { phoneNumber ->
                canvas.drawText("Tél: $phoneNumber", margin, y, normalPaint)
                y += 15f
            }
            client.address?.let { address ->
                canvas.drawText(address, margin, y, normalPaint)
                y += 15f
            }
            client.ice?.let { ice ->
                canvas.drawText("ICE: $ice", margin, y, normalPaint)
                y += 15f
            }
        }
        
        // Colonne droite - Informations Facture (ajustée pour plus d'espace à droite)
        val rightColumnX = pageWidth - margin - 200f
        val rightValueX = pageWidth - margin - 80f
        var rightY = startY
        
        val documentType = InvoiceType.fromString(companyInfo?.invoiceType ?: "FACTURE")
        val labelText = when(documentType) {
            InvoiceType.BON_LIVRAISON -> "N° Bon:"
            else -> "N° ${documentType.displayName}:"
        }
        canvas.drawText(labelText, rightColumnX, rightY, headerPaint)
        canvas.drawText("#${invoice.id}", rightValueX, rightY, normalPaint)
        rightY += 20f
        
        canvas.drawText("Date:", rightColumnX, rightY, headerPaint)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        canvas.drawText(dateFormat.format(Date(invoice.dateTimestamp)), rightValueX, rightY, normalPaint)
        rightY += 20f
        
        canvas.drawText("Statut:", rightColumnX, rightY, headerPaint)
        canvas.drawText(if (invoice.isPaid) "PAYÉE" else "EN ATTENTE", rightValueX, rightY, normalPaint)
        
        return maxOf(y, rightY) + 10f
    }
    
    private fun drawItemsTable(canvas: Canvas, headerPaint: Paint, normalPaint: Paint, linePaint: Paint, margin: Float, startY: Float, pageWidth: Float): Float {
        var y = startY
        val tableWidth = pageWidth - (2 * margin)
        val rowHeight = 25f
        val documentType = InvoiceType.fromString(companyInfo?.invoiceType ?: "FACTURE")
        
        // En-têtes du tableau selon le type de document
        val (colWidths, headers) = if (documentType == InvoiceType.FACTURE) {
            Pair(
                floatArrayOf(200f, 80f, 80f, 80f, 85f), // Description, Qté, Prix unit. HT, TVA, Total TTC
                arrayOf("Description", "Qté", "Prix unit. HT", "TVA", "Total TTC")
            )
        } else {
            Pair(
                floatArrayOf(250f, 80f, 80f, 115f), // Description, Qté, Prix unit. HT, Total HT
                arrayOf("Description", "Qté", "Prix unit. HT", "Total HT")
            )
        }
        
        var x = margin
        
        // Ligne d'en-tête
        canvas.drawRect(margin, y, pageWidth - margin, y + rowHeight, linePaint)
        
        for (i in headers.indices) {
            canvas.drawText(headers[i], x + 5f, y + 16f, headerPaint)
            x += colWidths[i]
            if (i < headers.size - 1) {
                canvas.drawLine(x, y, x, y + rowHeight, linePaint)
            }
        }
        y += rowHeight
        
        // Lignes des articles
        if (invoiceItems.isNotEmpty()) {
            invoiceItems.forEach { item ->
                val product = products.find { it.id == item.productId }
                x = margin
                
                // Lignes du tableau
                canvas.drawRect(margin, y, pageWidth - margin, y + rowHeight, linePaint)
                
                // Description
                val productName = if (item.productId == 0L) {
                    // Article libre
                    item.description ?: "Article libre"
                } else {
                    // Produit de l'inventaire
                    product?.name ?: "Produit #${item.productId}"
                }
                canvas.drawText(productName, x + 5f, y + 16f, normalPaint)
                x += colWidths[0]
                canvas.drawLine(x, y, x, y + rowHeight, linePaint)
                
                // Quantité
                canvas.drawText(item.quantity.toString(), x + 5f, y + 16f, normalPaint)
                x += colWidths[1]
                canvas.drawLine(x, y, x, y + rowHeight, linePaint)
                
                // Prix unitaire
                canvas.drawText("${String.format("%.2f", item.unitPrice)} Dhs", x + 5f, y + 16f, normalPaint)
                x += colWidths[2]
                canvas.drawLine(x, y, x, y + rowHeight, linePaint)
                
                if (documentType == InvoiceType.FACTURE) {
                    // TVA (seulement pour les factures)
                    val tvaRate = companyInfo?.tvaRate ?: 20.0
                    canvas.drawText("${String.format("%.1f", tvaRate)}%", x + 5f, y + 16f, normalPaint)
                    x += colWidths[3]
                    canvas.drawLine(x, y, x, y + rowHeight, linePaint)
                    
                    // Total TTC
                    val totalHT = item.quantity * item.unitPrice
                    val totalTTC = totalHT * (1 + tvaRate / 100.0)
                    canvas.drawText("${String.format("%.2f", totalTTC)} Dhs", x + 5f, y + 16f, normalPaint)
                } else {
                    // Total HT (pour bon de livraison)
                    val totalHT = item.quantity * item.unitPrice
                    canvas.drawText("${String.format("%.2f", totalHT)} Dhs", x + 5f, y + 16f, normalPaint)
                }
                
                y += rowHeight
            }
        } else if (sale != null) {
            // Fallback avec les données de vente legacy
            x = margin
            canvas.drawRect(margin, y, pageWidth - margin, y + rowHeight, linePaint)
            
            val product = products.find { it.id == sale.productId }
            canvas.drawText(product?.name ?: "Produit #${sale.productId}", x + 5f, y + 16f, normalPaint)
            x += colWidths[0]
            canvas.drawLine(x, y, x, y + rowHeight, linePaint)
            
            canvas.drawText(sale.quantity.toString(), x + 5f, y + 16f, normalPaint)
            x += colWidths[1]
            canvas.drawLine(x, y, x, y + rowHeight, linePaint)
            
            canvas.drawText("${String.format("%.2f", sale.unitPrice)} Dhs", x + 5f, y + 16f, normalPaint)
            x += colWidths[2]
            canvas.drawLine(x, y, x, y + rowHeight, linePaint)
            
            if (documentType == InvoiceType.FACTURE) {
                val tvaRate = companyInfo?.tvaRate ?: 20.0
                canvas.drawText("${String.format("%.1f", tvaRate)}%", x + 5f, y + 16f, normalPaint)
                x += colWidths[3]
                canvas.drawLine(x, y, x, y + rowHeight, linePaint)
                
                canvas.drawText("${String.format("%.2f", sale.totalPrice)} Dhs", x + 5f, y + 16f, normalPaint)
            } else {
                canvas.drawText("${String.format("%.2f", sale.totalPrice)} Dhs", x + 5f, y + 16f, normalPaint)
            }
            
            y += rowHeight
        } else {
            // Aucun article disponible - afficher un message d'information
            x = margin
            canvas.drawRect(margin, y, pageWidth - margin, y + rowHeight, linePaint)
            
            canvas.drawText("Aucun article disponible pour cette facture", x + 5f, y + 16f, normalPaint)
            for (i in 1 until colWidths.size) {
                x += colWidths[i - 1]
                canvas.drawLine(x, y, x, y + rowHeight, linePaint)
            }
            
            y += rowHeight
        }
        
        return y
    }
    
    private fun drawTotals(canvas: Canvas, headerPaint: Paint, normalPaint: Paint, margin: Float, startY: Float, pageWidth: Float): Float {
        var y = startY
        val rightColumnX = pageWidth - margin - 200f
        val rightValueX = pageWidth - margin - 80f
        val documentType = InvoiceType.fromString(companyInfo?.invoiceType ?: "FACTURE")
        
        if (documentType == InvoiceType.FACTURE) {
            // Mode Facture avec TVA
            // Sous-total HT
            canvas.drawText("Sous-total HT:", rightColumnX, y, normalPaint)
            canvas.drawText("${String.format("%.2f", invoice.totalAmount)} Dhs", rightValueX, y, normalPaint)
            y += 20f
            
            // TVA
            val tvaRate = (companyInfo?.tvaRate ?: 20.0) / 100.0
            val tva = invoice.totalAmount * tvaRate
            canvas.drawText("TVA (${String.format("%.1f", companyInfo?.tvaRate ?: 20.0)}%):", rightColumnX, y, normalPaint)
            canvas.drawText("${String.format("%.2f", tva)} Dhs", rightValueX, y, normalPaint)
            y += 20f
            
            // Total TTC
            val totalTTC = invoice.totalAmount + tva
            canvas.drawText("TOTAL TTC:", rightColumnX, y, headerPaint)
            canvas.drawText("${String.format("%.2f", totalTTC)} Dhs", rightValueX, y, headerPaint)
        } else {
            // Mode Bon de Livraison - toujours HT
            canvas.drawText("TOTAL HT:", rightColumnX, y, headerPaint)
            canvas.drawText("${String.format("%.2f", invoice.totalAmount)} Dhs", rightValueX, y, headerPaint)
        }
        
        return y
    }
    
    private fun drawFooterInfo(canvas: Canvas, smallPaint: Paint, normalPaint: Paint, margin: Float, startY: Float, pageWidth: Float, pageHeight: Float) {
        var y = pageHeight - margin - 60f
        
        // Conditions de paiement
        canvas.drawText("Conditions de paiement: Paiement à 30 jours", margin, y, smallPaint)
        y += 15f
        
        // Informations bancaires si disponibles
        companyInfo?.bankAccount?.let { account ->
            canvas.drawText("Compte bancaire: $account", margin, y, smallPaint)
            y += 15f
        }
        
        // Mentions légales
        canvas.drawText("Merci pour votre confiance !", margin, y, smallPaint)
    }
}
