package com.example.appstock.viewmodel

import androidx.lifecycle.*
import com.example.appstock.data.Sale
import com.example.appstock.data.SaleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ViewModel for [Sale] operations.
 */
class SaleViewModel(private val repository: SaleRepository) : ViewModel() {
    val allSales: LiveData<List<Sale>> = repository.allSales

    fun getSalesForProduct(productId: Long): LiveData<List<Sale>> = repository.getSalesForProduct(productId)

    fun insert(sale: Sale) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(sale)
    }

    fun update(sale: Sale) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(sale)
    }

    fun delete(sale: Sale) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(sale)
    }
}