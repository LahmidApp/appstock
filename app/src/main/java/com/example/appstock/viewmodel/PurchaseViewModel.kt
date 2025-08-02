package com.example.appstock.viewmodel

import androidx.lifecycle.*
import com.example.appstock.data.Purchase
import com.example.appstock.data.PurchaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ViewModel for [Purchase] operations.
 */
class PurchaseViewModel(private val repository: PurchaseRepository) : ViewModel() {
    val allPurchases: LiveData<List<Purchase>> = repository.allPurchases

    fun getPurchasesForProduct(productId: Long): LiveData<List<Purchase>> = repository.getPurchasesForProduct(productId)

    fun insert(purchase: Purchase) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(purchase)
    }

    fun update(purchase: Purchase) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(purchase)
    }

    fun delete(purchase: Purchase) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(purchase)
    }
}