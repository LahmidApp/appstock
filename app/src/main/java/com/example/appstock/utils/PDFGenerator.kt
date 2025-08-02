package com.example.appstock.utils

import android.content.Context
// import android.graphics.Bitmap // Non utilisé, peut être supprimé si ce n'est pas pour une future fonctionnalité
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import com.example.appstock.data.Invoice
import com.example.appstock.data.Quote
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Utility class for generating PDF documents
 */
object PDFGenerator {

    private const val PAGE_WIDTH = 595 // A4 width in points
    private const val PAGE_HEIGHT = 842 // A4 height in points
    private const val MARGIN = 50

    /**
     * Generate PDF for invoice
     */
    fun generateInvoicePDF(context: Context, invoice: Invoice, items: List<Any>, settings: com.example.appstock.data.LibrarySettings? = null): String? {
        return try {
            val document = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
            val page = document.startPage(pageInfo)
            val canvas = page.canvas
            val paint = Paint()

            // Draw invoice content with dynamic header
            drawInvoiceContentWithHeader(canvas, paint, invoice, items, settings)

            document.finishPage(page)

            val fileName = "invoice_${invoice.id}_${System.currentTimeMillis()}.pdf"
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
            FileOutputStream(file).use { outputStream ->
                document.writeTo(outputStream)
            }
            document.close()
            file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun drawInvoiceContentWithHeader(canvas: Canvas, paint: Paint, invoice: Invoice, items: List<Any>, settings: com.example.appstock.data.LibrarySettings?) {
        var y = 40f
        paint.textSize = 18f
        paint.isFakeBoldText = true
        // Logo (optionnel)
        if (settings?.logoPath != null) {
            try {
                val logoFile = java.io.File(settings.logoPath)
                if (logoFile.exists()) {
                    val bitmap = android.graphics.BitmapFactory.decodeFile(settings.logoPath)
                    bitmap?.let {
                        canvas.drawBitmap(it, MARGIN.toFloat(), y, paint)
                        y += (it.height + 10).toFloat()
                    }
                }
            } catch (_: Exception) {}
        }
        // Nom de la librairie
        canvas.drawText(settings?.name ?: "Nom de la librairie", MARGIN.toFloat(), y, paint)
        y += 24f
        // Adresse, téléphone, email
        paint.textSize = 12f
        paint.isFakeBoldText = false
        if (!settings?.address.isNullOrBlank()) {
            canvas.drawText(settings!!.address!!, MARGIN.toFloat(), y, paint)
            y += 16f
        }
        if (!settings?.phone.isNullOrBlank()) {
            canvas.drawText("Tél: ${settings!!.phone!!}", MARGIN.toFloat(), y, paint)
            y += 16f
        }
        if (!settings?.email.isNullOrBlank()) {
            canvas.drawText("Email: ${settings!!.email!!}", MARGIN.toFloat(), y, paint)
            y += 16f
        }
        y += 10f
        paint.textSize = 24f
        paint.isFakeBoldText = true
        canvas.drawText("FACTURE", MARGIN.toFloat(), y, paint)
        y += 30f
        paint.textSize = 14f
        paint.isFakeBoldText = false
        canvas.drawText("Numéro: ${invoice.id}", MARGIN.toFloat(), y, paint)
        y += 20f
        val dateFormat = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
        canvas.drawText("Date: ${dateFormat.format(java.util.Date(invoice.dateTimestamp))}", MARGIN.toFloat(), y, paint)
        y += 20f
        val statusText = if (invoice.isPaid) "Payée" else "En attente"
        canvas.drawText("Statut: $statusText", MARGIN.toFloat(), y, paint)
        y += 30f
        // Table header
        paint.isFakeBoldText = true
        canvas.drawText("Article", MARGIN.toFloat(), y, paint)
        canvas.drawText("Qté", 300f, y, paint)
        canvas.drawText("Prix Unit.", 380f, y, paint)
        canvas.drawText("Total", 480f, y, paint)
        y += 20f
        paint.isFakeBoldText = false
        canvas.drawLine(MARGIN.toFloat(), y - 10f, (PAGE_WIDTH - MARGIN).toFloat(), y - 10f, paint)
        // TODO: dessiner les items (voir drawInvoiceContent)
        // Total
        y += 30f
        paint.isFakeBoldText = true
        canvas.drawText("Total: ${invoice.totalAmount} ${settings?.currency ?: "Dhs"}", 400f, y, paint)
        // TVA
        if (settings != null && settings.taxRate > java.math.BigDecimal.ZERO) {
            paint.textSize = 12f
            paint.isFakeBoldText = false
            y += 20f
            canvas.drawText("TVA appliquée: ${settings.taxRate}%", 400f, y, paint)
        }
        // Footer (mentions légales, etc. à adapter)
        paint.textSize = 10f
        paint.isFakeBoldText = false
        canvas.drawText("Merci pour votre confiance.", MARGIN.toFloat(), (PAGE_HEIGHT - 40).toFloat(), paint)
    }

    /**
     * Generate PDF for quote
     */
    fun generateQuotePDF(context: Context, quote: Quote, items: List<Any>): String? {
        return try {
            val document = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
            val page = document.startPage(pageInfo)
            val canvas = page.canvas
            val paint = Paint()

            // Draw quote content
            drawQuoteContent(canvas, paint, quote, items)

            document.finishPage(page)

            // Save to file
            // CORRIGÉ: Utilise quote.id comme numéro de devis pour le nom du fichier
            val fileName = "quote_${quote.id}_${System.currentTimeMillis()}.pdf"
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
            FileOutputStream(file).use { outputStream -> // Utilisation de .use
                document.writeTo(outputStream)
            }
            document.close()

            file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun drawInvoiceContent(canvas: Canvas, paint: Paint, invoice: Invoice, items: List<Any>) {
        paint.textSize = 24f
        paint.isFakeBoldText = true
        canvas.drawText("FACTURE", MARGIN.toFloat(), 80f, paint)

        paint.textSize = 14f
        paint.isFakeBoldText = false

        var yPosition = 120f
        // CORRIGÉ: Utilise invoice.id
        canvas.drawText("Numéro: ${invoice.id}", MARGIN.toFloat(), yPosition, paint)
        yPosition += 25f

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        // CORRIGÉ: Utilise invoice.dateTimestamp et le convertit en Date
        canvas.drawText("Date: ${dateFormat.format(Date(invoice.dateTimestamp))}", MARGIN.toFloat(), yPosition, paint)
        yPosition += 25f

        // COMMENTÉ: invoice.dueDate n'existe pas dans votre classe Invoice
        // Si vous ajoutez dueDate à Invoice.kt (par exemple, dueDateTimestamp: Long?), décommentez et adaptez :
        // invoice.dueDateTimestamp?.let {
        //     canvas.drawText("Échéance: ${dateFormat.format(Date(it))}", MARGIN.toFloat(), yPosition, paint)
        //     yPosition += 25f
        // }

        // CORRIGÉ: Déduit le statut à partir de invoice.isPaid
        val statusText = if (invoice.isPaid) "Payée" else "En attente"
        canvas.drawText("Statut: $statusText", MARGIN.toFloat(), yPosition, paint)
        yPosition += 50f

        // Draw items table header
        paint.isFakeBoldText = true
        canvas.drawText("Article", MARGIN.toFloat(), yPosition, paint)
        canvas.drawText("Qté", 300f, yPosition, paint)
        canvas.drawText("Prix Unit.", 380f, yPosition, paint)
        canvas.drawText("Total", 480f, yPosition, paint)
        yPosition += 30f

        paint.isFakeBoldText = false
        // Draw line
        canvas.drawLine(MARGIN.toFloat(), yPosition - 10f, (PAGE_WIDTH - MARGIN).toFloat(), yPosition - 10f, paint)

        // TODO: Vous devrez implémenter la logique pour dessiner les 'items' ici
        // Exemple (très basique, à adapter à la structure de vos items) :
        // items.forEach { item ->
        //     // Supposons que 'item' a des propriétés comme 'name', 'quantity', 'unitPrice', 'total'
        //     // canvas.drawText(item.name.toString(), MARGIN.toFloat(), yPosition, paint)
        //     // canvas.drawText(item.quantity.toString(), 300f, yPosition, paint)
        //     // ... et ainsi de suite
        //     yPosition += 20f // Incrémenter yPosition pour chaque item
        // }
        // yPosition += 30f // Espace après les items avant le total

        // Draw total
        // La propriété invoice.totalAmount existe déjà et est utilisée correctement
        paint.isFakeBoldText = true
        canvas.drawText("Total: ${invoice.totalAmount} Dhs", 400f, yPosition, paint)
    }

    private fun drawQuoteContent(canvas: Canvas, paint: Paint, quote: Quote, items: List<Any>) {
        paint.textSize = 24f
        paint.isFakeBoldText = true
        canvas.drawText("DEVIS", MARGIN.toFloat(), 80f, paint)

        paint.textSize = 14f
        paint.isFakeBoldText = false

        var yPosition = 120f
        // CORRIGÉ: Utilise quote.id
        canvas.drawText("Numéro: ${quote.id}", MARGIN.toFloat(), yPosition, paint)
        yPosition += 25f

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        // CORRIGÉ: Utilise quote.dateTimestamp et le convertit en Date
        canvas.drawText("Date: ${dateFormat.format(Date(quote.dateTimestamp))}", MARGIN.toFloat(), yPosition, paint)
        yPosition += 25f

        // COMMENTÉ: quote.validUntil n'existe pas dans votre classe Quote
        // Si vous ajoutez validUntil à Quote.kt (par exemple, validUntilTimestamp: Long?), décommentez et adaptez :
        // quote.validUntilTimestamp?.let {
        //    canvas.drawText("Valide jusqu'au: ${dateFormat.format(Date(it))}", MARGIN.toFloat(), yPosition, paint)
        //    yPosition += 25f
        // }

        // CORRIGÉ: La propriété quote.status existe déjà et est utilisée correctement
        canvas.drawText("Statut: ${quote.status}", MARGIN.toFloat(), yPosition, paint)
        yPosition += 50f

        // Draw items table header
        paint.isFakeBoldText = true
        canvas.drawText("Article", MARGIN.toFloat(), yPosition, paint)
        canvas.drawText("Qté", 300f, yPosition, paint)
        canvas.drawText("Prix Unit.", 380f, yPosition, paint)
        canvas.drawText("Total", 480f, yPosition, paint)
        yPosition += 30f

        paint.isFakeBoldText = false
        // Draw line
        canvas.drawLine(MARGIN.toFloat(), yPosition - 10f, (PAGE_WIDTH - MARGIN).toFloat(), yPosition - 10f, paint)

        // TODO: Vous devrez implémenter la logique pour dessiner les 'items' ici (similaire à drawInvoiceContent)
        // yPosition += 30f // Espace après les items avant le total

        // Draw total
        // La propriété quote.totalAmount existe déjà et est utilisée correctement
        paint.isFakeBoldText = true
        canvas.drawText("Total: ${quote.totalAmount} Dhs", 400f, yPosition, paint)
    }
}
