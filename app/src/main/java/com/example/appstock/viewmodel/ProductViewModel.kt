package com.example.appstock.viewmodel

import androidx.lifecycle.*
import com.example.appstock.data.Product
import com.example.appstock.data.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel for managing [Product] data. It interacts with [ProductRepository]
 * to perform database operations and exposes LiveData for the UI to observe.
 */
class ProductViewModel(private val repository: ProductRepository) : ViewModel() {
    val allProducts: LiveData<List<Product>> = repository.allProducts
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    val error = MutableStateFlow<String?>(null)

    init {
        allProducts.observeForever { list ->
            _products.value = list ?: emptyList()
        }
    }

    fun loadProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // plus besoin de charger manuellement, mais on garde pour compatibilité UI
                _products.value = repository.allProducts.value ?: emptyList()
                error.value = null
            } catch (e: Exception) {
                error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun insert(product: Product) = viewModelScope.launch {
        repository.insert(product)
    }

    fun update(product: Product) = viewModelScope.launch {
        repository.update(product)
    }

    fun addProduct(
        name: String,
        description: String?,
        price: Double, // CHANGED
        costPrice: Double, //
        quantity: Int,
        category: String?,
        supplier: String?,
        minStockLevel: Int?
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val product = Product(
                    name = name,
                    description = description,
                    price = price,
                    costPrice = costPrice,
                    quantity = quantity,
                    category = category,
                    supplier = supplier,
                    minStockLevel = minStockLevel,
                    barcode = "",
                    qrCode = ""
                )
                repository.insert(product)
                error.value = null
            } catch (e: Exception) {
                error.value = e.message
            }
        }
    }

    fun deleteProduct(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val product = repository.allProducts.value?.find { it.id == id }
                if (product != null) {
                    repository.delete(product)
                    error.value = null
                } else {
                    error.value = "Produit introuvable"
                }
            } catch (e: Exception) {
                error.value = e.message
            }
        }
    }

    fun searchProducts(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val all = repository.allProducts.value ?: emptyList()
                _products.value = all.filter {
                    it.name.contains(query, ignoreCase = true) ||
                    (it.category?.contains(query, ignoreCase = true) ?: false)
                }
                error.value = null
            } catch (e: Exception) {
                error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ...existing code...
}