package com.example.appstock.ui.products

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appstock.R
import androidx.compose.material.icons.filled.Add // Make sure this import is present

import com.example.appstock.data.AppDatabase
import com.example.appstock.data.ProductRepository
import com.example.appstock.viewmodel.ProductViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.appstock.viewmodel.ProductViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * Fragment displaying a list of products. Provides a FAB to add new products and uses
 * [ProductAdapter] to present each product.
 */

class ProductListFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductAdapter
    private lateinit var productViewModel: ProductViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_product_list, container, false)
        recyclerView = view.findViewById(R.id.recycler_view_products)
        val fab: FloatingActionButton = view.findViewById(R.id.fab_add_product)

        adapter = ProductAdapter { product ->
            val intent = Intent(requireContext(), AddEditProductActivity::class.java)
            intent.putExtra(AddEditProductActivity.EXTRA_PRODUCT_ID, product.id)
            startActivity(intent)
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))

        fab.setOnClickListener {
            val intent = Intent(requireContext(), AddEditProductActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val database = AppDatabase.getDatabase(requireContext())
        val repository = ProductRepository(database.productDao())
        val factory = ProductViewModelFactory(repository)
        productViewModel = ViewModelProvider(this, factory)[ProductViewModel::class.java]
        productViewModel.allProducts.observe(viewLifecycleOwner) { products ->
            adapter.submitList(products)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: android.view.Menu, inflater: android.view.MenuInflater) {
        inflater.inflate(R.menu.product_list_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_export_pdf -> {
                exportToPdf()
                true
            }
            R.id.action_export_excel -> {
                exportToExcel()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Export the product list to a PDF file using the iText library.
     */
    private fun exportToPdf() {
        val context = requireContext()
        val products = adapter.currentList
        if (products.isEmpty()) {
            Toast.makeText(context, "Aucun produit à exporter", Toast.LENGTH_SHORT).show()
            return
        }
        val fileName = "produits_${System.currentTimeMillis()}.pdf"
        val fileDir = context.getExternalFilesDir(null)
        val file = java.io.File(fileDir, fileName)
        try {
            // Use Android native PDF generation instead of iTextPDF
            val document = android.graphics.pdf.PdfDocument()
            val pageInfo = android.graphics.pdf.PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
            val page = document.startPage(pageInfo)
            val canvas = page.canvas
            val paint = android.graphics.Paint().apply {
                textSize = 16f
                color = android.graphics.Color.BLACK
            }
            val titlePaint = android.graphics.Paint().apply {
                textSize = 20f
                color = android.graphics.Color.BLACK
                isFakeBoldText = true
            }
            
            // Draw title
            canvas.drawText("Liste des produits", 50f, 50f, titlePaint)
            
            // Draw table headers
            var yPosition = 100f
            val lineHeight = 25f
            val colWidths = floatArrayOf(150f, 100f, 100f, 150f) // Nom, Prix, Quantité, Code barre
            var xPosition = 50f
            
            paint.isFakeBoldText = true
            canvas.drawText("Nom", xPosition, yPosition, paint)
            xPosition += colWidths[0]
            canvas.drawText("Prix", xPosition, yPosition, paint)
            xPosition += colWidths[1]
            canvas.drawText("Quantité", xPosition, yPosition, paint)
            xPosition += colWidths[2]
            canvas.drawText("Code barre", xPosition, yPosition, paint)
            
            paint.isFakeBoldText = false
            yPosition += lineHeight
            
            // Draw table rows
            for (product in products) {
                if (yPosition > 800) break // Avoid going off page
                xPosition = 50f
                canvas.drawText(product.name, xPosition, yPosition, paint)
                xPosition += colWidths[0]
                canvas.drawText("${product.price}Dhs", xPosition, yPosition, paint)
                xPosition += colWidths[1]
                canvas.drawText(product.stock.toString(), xPosition, yPosition, paint)
                xPosition += colWidths[2]
                canvas.drawText(product.barcode, xPosition, yPosition, paint)
                yPosition += lineHeight
            }
            
            document.finishPage(page)
            
            // Write to file
            java.io.FileOutputStream(file).use { outputStream ->
                document.writeTo(outputStream)
            }
            document.close()
            
            Toast.makeText(context, "PDF exporté: ${file.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Erreur lors de l'exportation PDF", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Export the product list to a CSV file using native Android I/O.
     * Replaced Apache POI with native CSV export for better compatibility.
     */
    private fun exportToExcel() {
        val context = requireContext()
        val products = adapter.currentList
        if (products.isEmpty()) {
            Toast.makeText(context, "Aucun produit à exporter", Toast.LENGTH_SHORT).show()
            return
        }
        val fileName = "produits_${System.currentTimeMillis()}.csv"
        val fileDir = context.getExternalFilesDir(null)
        val file = java.io.File(fileDir, fileName)
        try {
            file.bufferedWriter().use { writer ->
                // En-tête CSV
                writer.write("Nom,Prix,Quantité,Code barre,QR Code,Description,Types,Prix coûtant,Stock minimum,Fournisseur\n")
                
                // Données des produits
                for (product in products) {
                    val line = listOf(
                        product.name.replace(",", ";"), // Remplacer les virgules pour éviter les conflits CSV
                        product.price.toString(),
                        product.stock.toString(),
                        product.barcode.replace(",", ";"),
                        product.qrCode.replace(",", ";"),
                        (product.description ?: "").replace(",", ";"),
                        product.types.joinToString("|").replace(",", ";"),
                        (product.costPrice ?: 0.0).toString(),
                        (product.minStockLevel ?: 0).toString(),
                        (product.supplier ?: "").replace(",", ";")
                    ).joinToString(",")
                    writer.write("$line\n")
                }
            }
            Toast.makeText(context, "CSV exporté: ${file.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Erreur lors de l'exportation CSV", Toast.LENGTH_SHORT).show()
        }
    }
}