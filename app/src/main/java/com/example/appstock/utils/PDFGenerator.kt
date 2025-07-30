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
    fun generateInvoicePDF(context: Context, invoice: Invoice, items: List<Any>): String? {
        return try {
            val document = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
            val page = document.startPage(pageInfo)
            val canvas = page.canvas
            val paint = Paint()

            // Draw invoice content
            drawInvoiceContent(canvas, paint, invoice, items)

            document.finishPage(page)

            // Save to file
            // CORRIGÉ: Utilise invoice.id comme numéro de facture pour le nom du fichier
            val fileName = "invoice_${invoice.id}_${System.currentTimeMillis()}.pdf"
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
            FileOutputStream(file).use { outputStream -> // Utilisation de .use pour la gestion automatique des ressources
                document.writeTo(outputStream)
            }
            document.close() // document.close() doit être appelé après writeTo et la fermeture du stream

            file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace() // Bonne pratique pour logger l'erreur
            null
        }
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
        canvas.drawText("Total: ${invoice.totalAmount} €", 400f, yPosition, paint)
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
        canvas.drawText("Total: ${quote.totalAmount} €", 400f, yPosition, paint)
    }
}
