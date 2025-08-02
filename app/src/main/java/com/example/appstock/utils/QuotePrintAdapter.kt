package com.example.appstock.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.os.ParcelFileDescriptor
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.graphics.Rect
import android.net.Uri
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import com.example.appstock.data.Quote
import com.example.appstock.data.Customer
import com.example.appstock.data.Product
import com.example.appstock.data.CompanyInfo
import com.example.appstock.data.InvoiceType

class QuotePrintAdapter(
    private val context: Context,
    private val quote: Quote,
    private val customer: Customer?,
    private val product: Product?,
    private val companyInfo: CompanyInfo?
) : PrintDocumentAdapter() {

    override fun onLayout(
        oldAttributes: PrintAttributes?,
        newAttributes: PrintAttributes,
        cancellationSignal: android.os.CancellationSignal?,
        callback: LayoutResultCallback,
        extras: Bundle?
    ) {
        if (cancellationSignal?.isCanceled == true) {
            callback.onLayoutCancelled()
            return
        }

        val documentType = InvoiceType.fromString(companyInfo?.invoiceType ?: "FACTURE")
        val fileName = "${documentType.fileName}_${quote.id}.pdf"
        
        val builder = PrintDocumentInfo.Builder(fileName)
            .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
            .setPageCount(1)

        val info = builder.build()
        callback.onLayoutFinished(info, true)
    }

    override fun onWrite(
        pages: Array<out PageRange>,
        destination: ParcelFileDescriptor,
        cancellationSignal: android.os.CancellationSignal?,
        callback: WriteResultCallback
    ) {
        try {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            drawQuoteContent(canvas)

            pdfDocument.finishPage(page)

            val fos = FileOutputStream(destination.fileDescriptor)
            pdfDocument.writeTo(fos)
            pdfDocument.close()
            fos.close()

            callback.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
        } catch (e: Exception) {
            callback.onWriteFailed(e.toString())
        }
    }

    private fun drawQuoteContent(canvas: Canvas) {
        val margin = 40f
        var y = 60f
        val pageWidth = canvas.width.toFloat()

        // Paint styles
        val titlePaint = Paint().apply {
            color = Color.BLACK
            textSize = 24f
            isFakeBoldText = true
        }

        val headerPaint = Paint().apply {
            color = Color.BLACK
            textSize = 16f
            isFakeBoldText = true
        }

        val normalPaint = Paint().apply {
            color = Color.BLACK
            textSize = 12f
        }

        val tablePaint = Paint().apply {
            color = Color.BLACK
            textSize = 11f
        }

        val linePaint = Paint().apply {
            color = Color.GRAY
            strokeWidth = 1f
        }

        // En-tête de l'entreprise
        companyInfo?.let { company ->
            drawCompanyHeader(canvas, company, margin, y, pageWidth, headerPaint, normalPaint)
            y += 120f
        } ?: run {
            y += 40f
        }

        // Titre du document (toujours "DEVIS" pour les devis)
        val documentTitle = "DEVIS"
        val titleWidth = titlePaint.measureText(documentTitle)
        canvas.drawText(documentTitle, (pageWidth - titleWidth) / 2f, y, titlePaint)
        y += 40f

        // Informations du devis et client en deux colonnes
        val leftColumnX = margin
        val rightColumnX = pageWidth - margin - 220f
        val rightValueX = pageWidth - margin - 80f

        // Colonne gauche - Informations Client
        canvas.drawText("DEVISÉ POUR:", leftColumnX, y, headerPaint)
        y += 20f
        
        customer?.let { client ->
            canvas.drawText(client.name, leftColumnX, y, normalPaint)
            y += 15f
            client.email?.let { email ->
                canvas.drawText(email, leftColumnX, y, normalPaint)
                y += 15f
            }
            client.phoneNumber?.let { phoneNumber ->
                canvas.drawText("Tél: $phoneNumber", leftColumnX, y, normalPaint)
                y += 15f
            }
            client.address?.let { address ->
                canvas.drawText(address, leftColumnX, y, normalPaint)
                y += 15f
            }
            client.ice?.let { ice ->
                canvas.drawText("ICE: $ice", leftColumnX, y, normalPaint)
                y += 15f
            }
        }
        
        // Colonne droite - Informations Devis (ajustée pour plus d'espace à droite)
        val rightY = y - 80f // Remonter pour aligner avec les infos client
        canvas.drawText("INFORMATIONS DEVIS", rightColumnX, rightY, headerPaint)
        var tempY = rightY + 20f
        canvas.drawText("Numéro:", rightColumnX, tempY, normalPaint)
        canvas.drawText("#${quote.id}", rightValueX, tempY, normalPaint)
        tempY += 15f
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        canvas.drawText("Date:", rightColumnX, tempY, normalPaint)
        canvas.drawText("${dateFormat.format(Date(quote.dateTimestamp))}", rightValueX, tempY, normalPaint)
        tempY += 15f
        canvas.drawText("Statut:", rightColumnX, tempY, normalPaint)
        canvas.drawText("${getStatusText(quote.status)}", rightValueX, tempY, normalPaint)
        tempY += 15f
        canvas.drawText("Validité:", rightColumnX, tempY, normalPaint)
        canvas.drawText("30 jours", rightValueX, tempY, normalPaint)

        y = maxOf(y, tempY) + 30f

        // Description si présente
        if (quote.description.isNotEmpty()) {
            canvas.drawText("DESCRIPTION:", margin, y, headerPaint)
            y += 20f
            canvas.drawText(quote.description, margin, y, normalPaint)
            y += 30f
        }

        // Tableau des articles
        drawQuoteItemsTable(canvas, quote, product, margin, y, pageWidth, tablePaint, linePaint)
        y += 120f

        // Totaux
        drawQuoteTotals(canvas, quote, companyInfo, margin, y, pageWidth, headerPaint, normalPaint)
        y += 80f

        // Conditions et notes
        drawQuoteFooter(canvas, margin, y, pageWidth, normalPaint)
    }

    private fun drawCompanyHeader(
        canvas: Canvas,
        company: CompanyInfo,
        margin: Float,
        y: Float,
        pageWidth: Float,
        headerPaint: Paint,
        normalPaint: Paint
    ) {
        var currentY = y
        
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
                            Rect(logoX.toInt(), (currentY - 10).toInt(), (logoX + logoSize).toInt(), (currentY + logoSize - 10).toInt()),
                            null
                        )
                        logoBitmap.recycle()
                    }
                }
            } catch (e: Exception) {
                // Log l'erreur pour déboguer
                android.util.Log.e("QuotePrintAdapter", "Erreur lors du chargement du logo: ${e.message}")
            }
        }
        
        // Nom de l'entreprise
        canvas.drawText(company.companyName, margin, currentY, headerPaint)
        currentY += 20f
        
        // Adresse
        company.address?.let { address ->
            canvas.drawText(address, margin, currentY, normalPaint)
            currentY += 15f
        }
        
        // Téléphone et Email sur la même ligne
        val contactInfo = mutableListOf<String>()
        company.phone?.let { contactInfo.add("Tél: $it") }
        company.email?.let { contactInfo.add("Email: $it") }
        
        if (contactInfo.isNotEmpty()) {
            canvas.drawText(contactInfo.joinToString(" - "), margin, currentY, normalPaint)
            currentY += 15f
        }
        
        // Informations comptables (droite)
        val rightX = pageWidth - 200f
        var rightY = y
        
        company.ice?.let { ice ->
            canvas.drawText("ICE: $ice", rightX, rightY, normalPaint)
            rightY += 15f
        }
        
        company.ifNumber?.let { ifNumber ->
            canvas.drawText("IF: $ifNumber", rightX, rightY, normalPaint)
            rightY += 15f
        }
        
        company.rc?.let { rc ->
            canvas.drawText("RC: $rc", rightX, rightY, normalPaint)
            rightY += 15f
        }
    }

    private fun drawQuoteItemsTable(
        canvas: Canvas,
        quote: Quote,
        product: Product?,
        margin: Float,
        y: Float,
        pageWidth: Float,
        tablePaint: Paint,
        linePaint: Paint
    ) {
        val tableWidth = pageWidth - 2 * margin
        val col1Width = tableWidth * 0.4f  // Description
        val col2Width = tableWidth * 0.15f // Quantité
        val col3Width = tableWidth * 0.2f  // Prix unitaire
        val col4Width = tableWidth * 0.25f // Total

        var currentY = y

        // En-têtes de tableau
        val headerPaint = Paint(tablePaint).apply { isFakeBoldText = true }
        
        canvas.drawText("DESCRIPTION", margin, currentY, headerPaint)
        canvas.drawText("QTÉ", margin + col1Width, currentY, headerPaint)
        canvas.drawText("PRIX UNITAIRE", margin + col1Width + col2Width, currentY, headerPaint)
        canvas.drawText("TOTAL", margin + col1Width + col2Width + col3Width, currentY, headerPaint)
        
        currentY += 5f
        
        // Ligne horizontale sous les en-têtes
        canvas.drawLine(margin, currentY, pageWidth - margin, currentY, linePaint)
        currentY += 20f

        // Ligne de l'article
        val productName = product?.name ?: "Produit inconnu"
        canvas.drawText(productName, margin, currentY, tablePaint)
        canvas.drawText(quote.quantity.toString(), margin + col1Width, currentY, tablePaint)
        canvas.drawText("${String.format("%.2f", quote.unitPrice)} Dhs", margin + col1Width + col2Width, currentY, tablePaint)
        canvas.drawText("${String.format("%.2f", quote.totalAmount)} Dhs", margin + col1Width + col2Width + col3Width, currentY, tablePaint)
        
        currentY += 20f
        
        // Ligne horizontale sous l'article
        canvas.drawLine(margin, currentY, pageWidth - margin, currentY, linePaint)
    }

    private fun drawQuoteTotals(
        canvas: Canvas,
        quote: Quote,
        companyInfo: CompanyInfo?,
        margin: Float,
        y: Float,
        pageWidth: Float,
        headerPaint: Paint,
        normalPaint: Paint
    ) {
        val rightColumnX = pageWidth - margin - 200f
        val rightValueX = pageWidth - margin - 80f
        var currentY = y

        // Sous-total
        canvas.drawText("Sous-total:", rightColumnX, currentY, normalPaint)
        canvas.drawText("${String.format("%.2f", quote.totalAmount)} Dhs", rightValueX, currentY, normalPaint)
        currentY += 20f

        // Pour les devis, utiliser le paramètre invoiceType pour déterminer HT ou TTC
        val documentType = InvoiceType.fromString(companyInfo?.invoiceType ?: "FACTURE")
        
        if (documentType == InvoiceType.FACTURE) {
            // Affichage TTC pour les devis avec paramètre "FACTURE"
            val tvaRate = companyInfo?.tvaRate ?: 20.0
            canvas.drawText("TVA (${String.format("%.1f", tvaRate)}%):", rightColumnX, currentY, normalPaint)
            val tva = quote.totalAmount * (tvaRate / 100.0)
            canvas.drawText("${String.format("%.2f", tva)} Dhs", rightValueX, currentY, normalPaint)
            currentY += 20f

            // Total TTC
            canvas.drawText("TOTAL TTC:", rightColumnX, currentY, headerPaint)
            val totalTTC = quote.totalAmount + tva
            canvas.drawText("${String.format("%.2f", totalTTC)} Dhs", rightValueX, currentY, headerPaint)
        } else {
            // Affichage HT pour les devis avec paramètre "BON_DE_LIVRAISON"
            canvas.drawText("TOTAL HT:", rightColumnX, currentY, headerPaint)
            canvas.drawText("${String.format("%.2f", quote.totalAmount)} Dhs", rightValueX, currentY, headerPaint)
        }
    }

    private fun drawQuoteFooter(
        canvas: Canvas,
        margin: Float,
        y: Float,
        pageWidth: Float,
        normalPaint: Paint
    ) {
        var currentY = y

        canvas.drawText("CONDITIONS:", margin, currentY, normalPaint)
        currentY += 20f
        
        canvas.drawText("• Ce devis est valable 30 jours à compter de sa date d'émission", margin, currentY, normalPaint)
        currentY += 15f
        canvas.drawText("• Les prix sont exprimés en dirhams toutes taxes comprises", margin, currentY, normalPaint)
        currentY += 15f
        canvas.drawText("• Acceptation du devis par signature et cachet", margin, currentY, normalPaint)
        currentY += 30f

        // Zone de signature
        canvas.drawText("Bon pour accord:", pageWidth - 200f, currentY, normalPaint)
        currentY += 15f
        canvas.drawText("Date et signature:", pageWidth - 200f, currentY, normalPaint)
    }

    private fun getStatusText(status: String): String {
        return when (status) {
            "pending" -> "En attente"
            "accepted" -> "Accepté"
            "rejected" -> "Refusé"
            "converted" -> "Converti"
            else -> status
        }
    }
}
