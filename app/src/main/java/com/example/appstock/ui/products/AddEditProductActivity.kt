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
import com.example.appstock.data.AppDatabase
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
    //private lateinit var categoryEditText: EditText
    private lateinit var typeEditText: EditText
    private lateinit var addTypeButton: Button
    private lateinit var typesListLayout: android.widget.LinearLayout
    private val typesList = mutableListOf<String>()
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
        typeEditText = findViewById(R.id.edit_text_type)
        addTypeButton = findViewById(R.id.button_add_type)
        typesListLayout = findViewById(R.id.layout_types_list)
        costPriceEditText = findViewById(R.id.edit_text_cost_price) // Assuming you add this ID
        minStockLevelEditText = findViewById(R.id.edit_text_min_stock) // Assuming you add this ID
        supplierEditText = findViewById(R.id.edit_text_supplier) // Assuming you add this ID
        // ... other initializations ...

        // Set up ViewModel
        val db = AppDatabase.getDatabase(this)
        val repository = ProductRepository(db.productDao())
        val factory = ProductViewModelFactory(repository)
        productViewModel = ViewModelProvider(this, factory)[ProductViewModel::class.java]

        // Check if editing existing product
        productId = intent.getLongExtra(EXTRA_PRODUCT_ID, -1L).takeIf { it != -1L }
        productId?.let { id ->
            CoroutineScope(Dispatchers.IO).launch {
                val product = repository.getProductById(id)
                product?.let {
                    currentProduct = it
                    runOnUiThread {
                        nameEditText.setText(it.name)
                        priceEditText.setText(it.price.toString())
                        quantityEditText.setText(it.stock.toString())
                        descriptionEditText.setText(it.description ?: "")
                        costPriceEditText.setText(it.costPrice?.toString() ?: "")
                        minStockLevelEditText.setText(it.minStockLevel?.toString() ?: "")
                        supplierEditText.setText(it.supplier ?: "")
                        
                        // Afficher les codes seulement s'ils ne sont pas vides
                        val qrCode = it.qrCode.takeIf { code -> code.isNotEmpty() } ?: ""
                        val barcode = it.barcode.takeIf { code -> code.isNotEmpty() } ?: ""
                        displayCodes(qrCode, barcode)
                        
                        // Stocker les codes dans les tags pour la sauvegarde
                        qrImageView.tag = qrCode
                        barcodeImageView.tag = barcode
                        
                        typesList.clear()
                        typesList.addAll(it.types)
                        refreshTypesList()
                    }
                }
            }
        }

        addTypeButton.setOnClickListener {
            val type = typeEditText.text.toString().trim()
            if (type.isNotEmpty() && !typesList.contains(type)) {
                typesList.add(type)
                typeEditText.text.clear()
                refreshTypesList()
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
            
            // Générer le QR Code seulement si les données ne sont pas vides
            if (qrData.isNotEmpty()) {
                val qrMatrix = writer.encode(qrData, BarcodeFormat.QR_CODE, 300, 300)
                val encoder = BarcodeEncoder()
                val qrBitmap: Bitmap = encoder.createBitmap(qrMatrix)
                qrImageView.setImageBitmap(qrBitmap)
            } else {
                // Effacer l'image si pas de données
                qrImageView.setImageDrawable(null)
            }
            
            // Générer le code-barres seulement si les données ne sont pas vides
            if (barcodeData.isNotEmpty()) {
                val barcodeMatrix = writer.encode(barcodeData, BarcodeFormat.CODE_128, 600, 150)
                val encoder = BarcodeEncoder()
                val barcodeBitmap: Bitmap = encoder.createBitmap(barcodeMatrix)
                barcodeImageView.setImageBitmap(barcodeBitmap)
            } else {
                // Effacer l'image si pas de données
                barcodeImageView.setImageDrawable(null)
            }
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
        val costPriceText = costPriceEditText.text.toString().trim()
        val minStockLevelText = minStockLevelEditText.text.toString().trim()
        val supplier = supplierEditText.text.toString().trim()

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
        val qrData = qrImageView.tag as? String ?: ""
        val barcodeData = barcodeImageView.tag as? String ?: ""
        
        // Si on modifie un produit existant et que les codes sont vides, on génère de nouveaux codes
        val finalQrData = if (qrData.isEmpty()) {
            "${name}_${System.currentTimeMillis()}"
        } else {
            qrData
        }
        
        val finalBarcodeData = if (barcodeData.isEmpty()) {
            "${name.hashCode()}_${System.currentTimeMillis().toString().takeLast(4)}"
        } else {
            barcodeData
        }

        val product = Product(
            id = currentProduct?.id ?: 0,
            name = name,
            price = price,
            stock = quantity,
            barcode = finalBarcodeData,
            qrCode = finalQrData,
            description = description,
            photoUri = currentProduct?.photoUri,
            types = typesList.toList(),
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

    private fun refreshTypesList() {
        typesListLayout.removeAllViews()
        for ((index, type) in typesList.withIndex()) {
            val typeView = android.widget.LinearLayout(this).apply {
                orientation = android.widget.LinearLayout.HORIZONTAL
                val textView = android.widget.TextView(this@AddEditProductActivity).apply {
                    text = type
                    setPadding(0, 0, 16, 0)
                }
                val removeBtn = android.widget.Button(this@AddEditProductActivity).apply {
                    text = "-"
                    setOnClickListener {
                        typesList.removeAt(index)
                        refreshTypesList()
                    }
                }
                addView(textView)
                addView(removeBtn)
            }
            typesListLayout.addView(typeView)
        }
    }

    companion object {
        const val EXTRA_PRODUCT_ID = "extra_product_id"
    }
}