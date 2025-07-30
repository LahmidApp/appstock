package com.example.appstock.data

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData

/**
 * Repository for [Sale] data.
 */
class SaleRepository(private val saleDao: SaleDao) {
    val allSales: LiveData<List<Sale>> = saleDao.getAllSales()

    fun getSalesForProduct(productId: Long): LiveData<List<Sale>> = saleDao.getSalesForProduct(productId)

    @WorkerThread
    suspend fun insert(sale: Sale): Long = saleDao.insert(sale)

    @WorkerThread
    suspend fun update(sale: Sale) = saleDao.update(sale)

    @WorkerThread
    suspend fun delete(sale: Sale) = saleDao.delete(sale)
}