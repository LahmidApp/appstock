
package com.example.appstock.ui.sales

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.appstock.R
import com.example.appstock.data.AppDatabase
import com.example.appstock.data.ProductRepository
import com.example.appstock.data.Sale
import com.example.appstock.data.SaleRepository
import com.example.appstock.viewmodel.ProductViewModel
import com.example.appstock.viewmodel.ProductViewModelFactory
import com.example.appstock.viewmodel.SaleViewModel
import com.example.appstock.viewmodel.SaleViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddEditSaleActivity : AppCompatActivity() {
    private var customers: List<com.example.appstock.data.Customer> = emptyList()
    private lateinit var productSpinner: Spinner
    private lateinit var typeSpinner: Spinner
    private lateinit var customerSpinner: Spinner
    private lateinit var quantityEditText: EditText
    private lateinit var unitPriceEditText: EditText
    private lateinit var saveButton: Button

    private lateinit var saleViewModel: SaleViewModel
    private lateinit var productViewModel: ProductViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_sale)


        productSpinner = findViewById(R.id.spinner_product)
        typeSpinner = findViewById(R.id.spinner_type)
        customerSpinner = findViewById(R.id.spinner_customer)
        quantityEditText = findViewById(R.id.edit_text_quantity)
        unitPriceEditText = findViewById(R.id.edit_text_unit_price)
        saveButton = findViewById(R.id.button_save_sale)


        val db = AppDatabase.getDatabase(this)
        val saleRepo = SaleRepository(db.saleDao())
        val productRepo = ProductRepository(db.productDao())
        val customerRepo = com.example.appstock.data.CustomerRepository(db.customerDao())
        saleViewModel = ViewModelProvider(this, SaleViewModelFactory(saleRepo))[SaleViewModel::class.java]
        productViewModel = ViewModelProvider(this, ProductViewModelFactory(productRepo))[ProductViewModel::class.java]
        val customerViewModel = ViewModelProvider(this, com.example.appstock.viewmodel.CustomerViewModelFactory(customerRepo))[com.example.appstock.viewmodel.CustomerViewModel::class.java]

        // Hold customers in a variable for later use

        // Populate product spinner
        productViewModel.allProducts.observe(this) { products ->
            val productNames = products.map { it.name }
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, productNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            productSpinner.adapter = adapter

            // When product changes, update type spinner
            productSpinner.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: android.widget.AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                    val selectedProduct = products.getOrNull(position)
                    val types = selectedProduct?.types ?: emptyList()
                    val typeAdapter = ArrayAdapter(this@AddEditSaleActivity, android.R.layout.simple_spinner_item, types)
                    typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    typeSpinner.adapter = typeAdapter
                }
                override fun onNothingSelected(parent: android.widget.AdapterView<*>) {
                    typeSpinner.adapter = ArrayAdapter(this@AddEditSaleActivity, android.R.layout.simple_spinner_item, emptyList<String>())
                }
            })
        }

        // Populate customer spinner
        customerViewModel.allCustomers.observe(this) { customerList ->
            customers = customerList ?: emptyList()
            android.util.Log.d("AddEditSaleActivity", "Loaded clients: ${customers.size}")
            val customerNames = customers.map { it.name }
            val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, customerNames)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            customerSpinner.adapter = adapter
        }

        saveButton.setOnClickListener {
            saveSale()
        }
    }

    private fun saveSale() {
        val selectedProductPosition = productSpinner.selectedItemPosition
        val selectedTypePosition = typeSpinner.selectedItemPosition
        val selectedCustomerPosition = customerSpinner.selectedItemPosition
        val quantityText = quantityEditText.text.toString().trim()
        val unitPriceText = unitPriceEditText.text.toString().trim()

        val products = productViewModel.allProducts.value ?: emptyList()
        // customers is now a variable in the activity

        if (selectedProductPosition == -1 || quantityText.isBlank() || unitPriceText.isBlank()) {
            Toast.makeText(this, "Veuillez remplir tous les champs obligatoires", Toast.LENGTH_SHORT).show()
            return
        }
        val product = products.getOrNull(selectedProductPosition)
        val quantity = quantityText.toIntOrNull()
        val unitPrice = unitPriceText.toDoubleOrNull()
        val customer = customers.getOrNull(selectedCustomerPosition)

        if (product == null || quantity == null || unitPrice == null) {
            Toast.makeText(this, "Valeur invalide", Toast.LENGTH_SHORT).show()
            return
        }

        val sale = Sale(
            productId = product.id,
            quantity = quantity,
            unitPrice = unitPrice,
            totalPrice = unitPrice * quantity,
            customerId = customer?.id,
            dateTimestamp = System.currentTimeMillis()
        )
        CoroutineScope(Dispatchers.IO).launch {
            saleViewModel.insert(sale)
            runOnUiThread {
                Toast.makeText(this@AddEditSaleActivity, "Vente enregistrée", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
