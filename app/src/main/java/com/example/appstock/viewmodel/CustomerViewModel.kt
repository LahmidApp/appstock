package com.example.appstock.viewmodel

import androidx.lifecycle.*
import com.example.appstock.data.Customer
import com.example.appstock.data.CustomerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ViewModel for [Customer] operations.
 */
class CustomerViewModel(private val repository: CustomerRepository) : ViewModel() {
    val allCustomers: LiveData<List<Customer>> = repository.allCustomers

    fun insert(customer: Customer) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(customer)
    }

    fun update(customer: Customer) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(customer)
    }

    fun delete(customer: Customer) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(customer)
    }
}

class CustomerViewModelFactory(private val repository: CustomerRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CustomerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CustomerViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}