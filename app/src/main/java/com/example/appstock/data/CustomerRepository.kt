package com.example.appstock.data

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData

/**
 * Repository for [Customer] data access.
 */
class CustomerRepository(private val customerDao: CustomerDao) {
    val allCustomers: LiveData<List<Customer>> = customerDao.getAllCustomers()

    @WorkerThread
    suspend fun getCustomerById(id: Long): Customer? = customerDao.getCustomerById(id)

    @WorkerThread
    suspend fun insert(customer: Customer): Long = customerDao.insert(customer)

    @WorkerThread
    suspend fun update(customer: Customer) = customerDao.update(customer)

    @WorkerThread
    suspend fun delete(customer: Customer) = customerDao.delete(customer)
}