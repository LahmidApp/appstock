package com.example.appstock.ui.customers

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.appstock.R
import com.example.appstock.data.Customer
import com.example.appstock.data.CustomerRepository
import com.example.appstock.data.AppDatabase
import com.example.appstock.viewmodel.CustomerViewModel
import com.example.appstock.viewmodel.CustomerViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddEditCustomerActivity : AppCompatActivity() {
    private lateinit var nameEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var addressEditText: EditText
    private lateinit var iceEditText: EditText
    private lateinit var saveButton: Button

    private var customerId: Long? = null
    private var currentCustomer: Customer? = null
    private lateinit var customerViewModel: CustomerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_customer)

        nameEditText = findViewById(R.id.edit_text_name)
        phoneEditText = findViewById(R.id.edit_text_phone)
        emailEditText = findViewById(R.id.edit_text_email)
        addressEditText = findViewById(R.id.edit_text_address)
        iceEditText = findViewById(R.id.edit_text_ice)
        saveButton = findViewById(R.id.button_save_customer)

        val db = AppDatabase.getDatabase(this)
        val repository = CustomerRepository(db.customerDao())
        val factory = CustomerViewModelFactory(repository)
        customerViewModel = ViewModelProvider(this, factory)[CustomerViewModel::class.java]

        customerId = intent.getLongExtra(EXTRA_CUSTOMER_ID, -1L).takeIf { it != -1L }
        customerId?.let { id ->
            CoroutineScope(Dispatchers.IO).launch {
                val customer = repository.getCustomerById(id)
                customer?.let {
                    currentCustomer = it
                    runOnUiThread {
                        nameEditText.setText(it.name)
                        phoneEditText.setText(it.phoneNumber ?: "")
                        emailEditText.setText(it.email ?: "")
                        addressEditText.setText(it.address ?: "")
                        iceEditText.setText(it.ice ?: "")
                    }
                }
            }
        }

        saveButton.setOnClickListener {
            saveCustomer()
        }
    }

    private fun saveCustomer() {
        val name = nameEditText.text.toString().trim()
        val phone = phoneEditText.text.toString().trim().takeIf { it.isNotEmpty() }
        val email = emailEditText.text.toString().trim().takeIf { it.isNotEmpty() }
        val address = addressEditText.text.toString().trim().takeIf { it.isNotEmpty() }
        val ice = iceEditText.text.toString().trim().takeIf { it.isNotEmpty() }

        if (name.isBlank()) {
            Toast.makeText(this, "Veuillez remplir le nom du client", Toast.LENGTH_SHORT).show()
            return
        }

        val customer = Customer(
            id = currentCustomer?.id ?: 0,
            name = name,
            phoneNumber = phone,
            email = email,
            address = address,
            ice = ice
        )
        CoroutineScope(Dispatchers.IO).launch {
            if (customerId != null && currentCustomer != null) {
                customerViewModel.update(customer)
                runOnUiThread { Toast.makeText(this@AddEditCustomerActivity, "Client mis à jour", Toast.LENGTH_SHORT).show() }
            } else {
                customerViewModel.insert(customer)
                runOnUiThread { Toast.makeText(this@AddEditCustomerActivity, "Client ajouté", Toast.LENGTH_SHORT).show() }
            }
            runOnUiThread { finish() }
        }
    }

    companion object {
        const val EXTRA_CUSTOMER_ID = "extra_customer_id"
    }
}
