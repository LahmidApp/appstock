package com.example.appstock.ui.products

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.semantics.text
import androidx.lifecycle.ViewModelProvider
import com.example.appstock.R
import com.example.appstock.data.LibraryDatabase
import com.example.appstock.data.Product
import com.example.appstock.data.ProductRepository
import com.example.appstock.viewmodel.ProductViewModel
import com.example.appstock.viewmodel.ProductViewModelFactory
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.text.toDoubleOrNull
import kotlin.text.toIntOrNull
import kotlin.text.trim

/**
 * Activity for adding or editing a product. Provides fields for name, price, quantity,
 * description and generates barcode and QR code using ZXing library. On save, the
 * product is inserted or updated in the database via [ProductViewModel].
 */

class AddEditProductActivity : AppCompatActivity() {
    private lateinit var nameEditText: EditText
    private lateinit var priceEditText: EditText
    private lateinit var quantityEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var qrImageView: ImageView
    private lateinit var barcodeImageView: ImageView
    private lateinit var generateQrButton: Button
    private lateinit var saveButton: Button
    private lateinit var categoryEditText: EditText
    private lateinit var costPriceEditText: EditText
    private lateinit var minStockLevelEditText: EditText
    private lateinit var supplierEditText: EditText

    private var productId: Long? = null
    private var currentProduct: Product? = null

    private lateinit var productViewModel: ProductViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_product)

        // Initialize UI elements
        nameEditText = findViewById(R.id.edit_text_name)
        priceEditText = findViewById(R.id.edit_text_price)
        quantityEditText = findViewById(R.id.edit_text_quantity)
        descriptionEditText = findViewById(R.id.edit_text_description)
        qrImageView = findViewById(R.id.image_qr)
        barcodeImageView = findViewById(R.id.image_barcode)
        generateQrButton = findViewById(R.id.button_generate_codes)
        saveButton = findViewById(R.id.button_save_product)
        categoryEditText = findViewById(R.id.edit_text_category) // Assuming you add this ID to your XML
        costPriceEditText = findViewById(R.id.edit_text_cost_price) // Assuming you add this ID
        minStockLevelEditText = findViewById(R.id.edit_text_min_stock) // Assuming you add this ID
        supplierEditText = findViewById(R.id.edit_text_supplier) // Assuming you add this ID
        // ... other initializations ...

        // Set up ViewModel
        val db = LibraryDatabase.getDatabase(this)
        val repository = ProductRepository(db.productDao())
        val factory = ProductViewModelFactory(repository)
        productViewModel = ViewModelProvider(this, factory)[ProductViewModel::class.java]

        // Check if editing existing product
        productId = intent.getLongExtra(EXTRA_PRODUCT_ID, -1L).takeIf { it != -1L }
        productId?.let { id ->
            // We need to load product from database; use coroutine
            CoroutineScope(Dispatchers.IO).launch {
                val product = repository.getProductById(id)
                product?.let {
                    currentProduct = it
                    runOnUiThread {
                        nameEditText.setText(it.name)
                        priceEditText.setText(it.price.toString())
                        quantityEditText.setText(it.quantity.toString())
                        descriptionEditText.setText(it.description ?: "")
                        // Generate code images from stored strings
                        costPriceEditText.setText(it.costPrice?.toString() ?: "")
                        minStockLevelEditText.setText(it.minStockLevel?.toString() ?: "")
                        supplierEditText.setText(it.supplier ?: "")
                        displayCodes(it.qrCode, it.barcode)
                    }
                }
            }
        }

        generateQrButton.setOnClickListener {
            // Generate codes based on input
            val name = nameEditText.text.toString().trim()
            if (name.isBlank()) {
                Toast.makeText(this, "Veuillez saisir le nom du produit", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val qrData = name + System.currentTimeMillis()
            val barcodeData = name.hashCode().toString() + System.currentTimeMillis().toString().takeLast(4)
            displayCodes(qrData, barcodeData)
            // We store codes in tags of buttons for later saving
            qrImageView.tag = qrData
            barcodeImageView.tag = barcodeData
        }

        saveButton.setOnClickListener {
            saveProduct()
        }
    }

    private fun displayCodes(qrData: String, barcodeData: String) {
        try {
            val writer = MultiFormatWriter()
            val qrMatrix = writer.encode(qrData, BarcodeFormat.QR_CODE, 300, 300)
            val barcodeMatrix = writer.encode(barcodeData, BarcodeFormat.CODE_128, 600, 150)
            val encoder = BarcodeEncoder()
            val qrBitmap: Bitmap = encoder.createBitmap(qrMatrix)
            val barcodeBitmap: Bitmap = encoder.createBitmap(barcodeMatrix)
            qrImageView.setImageBitmap(qrBitmap)
            barcodeImageView.setImageBitmap(barcodeBitmap)
        } catch (e: WriterException) {
            e.printStackTrace()
            Toast.makeText(this, "Erreur lors de la génération des codes", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveProduct() {
        val name = nameEditText.text.toString().trim()
        val priceText = priceEditText.text.toString().trim()
        val quantityText = quantityEditText.text.toString().trim()
        val description = descriptionEditText.text.toString().trim().takeIf { it.isNotEmpty() }
        val category = categoryEditText.text.toString().trim() // Or make it nullable if your Product allows
        val costPriceText = costPriceEditText.text.toString().trim()
        val minStockLevelText = minStockLevelEditText.text.toString().trim()
        val supplier = supplierEditText.text.toString().trim() // Or make it nullable


        if (name.isBlank() || priceText.isBlank() || quantityText.isBlank()) {
            Toast.makeText(this, "Veuillez remplir tous les champs obligatoires", Toast.LENGTH_SHORT).show()
            return
        }
        val price = priceText.toDoubleOrNull()
        val quantity = quantityText.toIntOrNull()
        val costPrice = costPriceText.toDoubleOrNull()
        val minStockLevel = minStockLevelText.toIntOrNull()

        if (price == null || costPrice == null || quantity == null) {
            Toast.makeText(this, "Prix ou quantité invalide", Toast.LENGTH_SHORT).show()
            return
        }
        val qrData = qrImageView.tag as? String ?: run {
            Toast.makeText(this, "Veuillez générer les codes", Toast.LENGTH_SHORT).show()
            return
        }
        val barcodeData = barcodeImageView.tag as? String ?: run {
            Toast.makeText(this, "Veuillez générer les codes", Toast.LENGTH_SHORT).show()
            return
        }

        val product = Product(
            id = currentProduct?.id ?: 0,
            name = name,
            price = price,
            quantity = quantity,
            barcode = barcodeData,
            qrCode = qrData,
            description = description,
            photoUri = currentProduct?.photoUri,
            category = category.takeIf { it.isNotEmpty() },
            costPrice = costPrice,
            minStockLevel = minStockLevel,
            supplier = supplier.takeIf { it.isNotEmpty() }
        )
        if (productId != null && currentProduct != null) {
            productViewModel.update(product)
            Toast.makeText(this, "Produit mis à jour", Toast.LENGTH_SHORT).show()
        } else {
            productViewModel.insert(product)
            Toast.makeText(this, "Produit ajouté", Toast.LENGTH_SHORT).show()
        }
        finish()
    }

    companion object {
        const val EXTRA_PRODUCT_ID = "extra_product_id"
    }
}