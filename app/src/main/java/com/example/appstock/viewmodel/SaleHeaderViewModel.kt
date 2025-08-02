package com.example.appstock.viewmodel

import androidx.lifecycle.*
import com.example.appstock.data.SaleHeader
import com.example.appstock.data.SaleItem
import com.example.appstock.data.SaleHeaderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ViewModel for [SaleHeader] operations with multiple items.
 */
class SaleHeaderViewModel(private val repository: SaleHeaderRepository) : ViewModel() {
    val allSaleHeaders: LiveData<List<SaleHeader>> = repository.allSaleHeaders

    fun getSaleItemsForSale(saleHeaderId: Long): LiveData<List<SaleItem>> = 
        repository.getSaleItemsForSale(saleHeaderId)

    fun insertSaleWithItems(saleHeader: SaleHeader, saleItems: List<SaleItem>) = 
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertSaleWithItems(saleHeader, saleItems)
        }

    fun insert(saleHeader: SaleHeader) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(saleHeader)
    }

    fun update(saleHeader: SaleHeader) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(saleHeader)
    }

    fun delete(saleHeader: SaleHeader) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(saleHeader)
    }
    
    fun updatePaymentStatus(saleId: Long, isPaid: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        repository.updatePaymentStatus(saleId, isPaid)
    }
}
