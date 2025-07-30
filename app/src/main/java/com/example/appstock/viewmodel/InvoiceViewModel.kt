package com.example.appstock.viewmodel

import androidx.lifecycle.*
import com.example.appstock.data.Invoice
import com.example.appstock.data.InvoiceItem
import com.example.appstock.data.InvoiceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ViewModel for managing [Invoice] and [InvoiceItem] entities.
 */
class InvoiceViewModel(private val repository: InvoiceRepository) : ViewModel() {
    val allInvoices: LiveData<List<Invoice>> = repository.allInvoices

    fun insert(invoice: Invoice) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(invoice)
    }

    fun update(invoice: Invoice) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(invoice)
    }

    fun delete(invoice: Invoice) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(invoice)
    }

    fun getItemsForInvoice(invoiceId: Long): LiveData<List<InvoiceItem>> = repository.getItemsForInvoice(invoiceId)

    fun insertItem(item: InvoiceItem) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertItem(item)
    }

    fun updateItem(item: InvoiceItem) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateItem(item)
    }

    fun deleteItem(item: InvoiceItem) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteItem(item)
    }
}

class InvoiceViewModelFactory(private val repository: InvoiceRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InvoiceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InvoiceViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}