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

import com.example.appstock.data.LibraryDatabase
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
        val database = LibraryDatabase.getDatabase(requireContext())
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
                val document = com.itextpdf.text.Document()
                com.itextpdf.text.pdf.PdfWriter.getInstance(document, java.io.FileOutputStream(file))
                document.open()
            document.add(com.itextpdf.text.Paragraph("Liste des produits"))
            document.add(com.itextpdf.text.Paragraph("\n"))
                val table = com.itextpdf.text.pdf.PdfPTable(4)
                table.addCell("Nom")
                table.addCell("Prix")
                table.addCell("Quantité")
                table.addCell("Code barre")
                for (p in products) {
                    table.addCell(p.name)
                    table.addCell(p.price.toString())
                    table.addCell(p.quantity.toString())
                    table.addCell(p.barcode)
                }
                document.add(table)
                document.close()
            Toast.makeText(context, "PDF exporté: ${file.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Erreur lors de l'exportation PDF", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Export the product list to an Excel file using Apache POI.
     */
    private fun exportToExcel() {
        val context = requireContext()
        val products = adapter.currentList
        if (products.isEmpty()) {
            Toast.makeText(context, "Aucun produit à exporter", Toast.LENGTH_SHORT).show()
            return
        }
        val fileName = "produits_${System.currentTimeMillis()}.xlsx"
        val fileDir = context.getExternalFilesDir(null)
        val file = java.io.File(fileDir, fileName)
        try {
                val workbook: org.apache.poi.ss.usermodel.Workbook = org.apache.poi.xssf.usermodel.XSSFWorkbook()
                val sheet = workbook.createSheet("Produits")
                val header = sheet.createRow(0)
                header.createCell(0).setCellValue("Nom")
                header.createCell(1).setCellValue("Prix")
                header.createCell(2).setCellValue("Quantité")
                header.createCell(3).setCellValue("Code barre")
                for ((index, p) in products.withIndex()) {
                    val row = sheet.createRow(index + 1)
                    row.createCell(0).setCellValue(p.name)
                    row.createCell(1).setCellValue(p.price)
                    row.createCell(2).setCellValue(p.quantity.toDouble())
                    row.createCell(3).setCellValue(p.barcode)
                }
                file.outputStream().use { fos ->
                    workbook.write(fos)
                }
                workbook.close()
            Toast.makeText(context, "Excel exporté: ${file.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Erreur lors de l'exportation Excel", Toast.LENGTH_SHORT).show()
        }
    }
}