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
    val recentCustomers: LiveData<List<Customer>> = repository.recentCustomers
    val loyalCustomers: LiveData<List<Customer>> = repository.loyalCustomers

    fun insert(customer: Customer) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(customer)
    }

    // Pour compatibilité avec l'appel de CustomersScreen (alias de insert)
    fun addCustomer(customer: Customer) = insert(customer)

    fun update(customer: Customer) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(customer)
    }

    fun delete(customer: Customer) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(customer)
    }
}