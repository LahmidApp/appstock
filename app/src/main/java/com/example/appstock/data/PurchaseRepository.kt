package com.example.appstock.data

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData

/**
 * Repository for [Purchase] data.
 */
class PurchaseRepository(private val purchaseDao: PurchaseDao) {
    val allPurchases: LiveData<List<Purchase>> = purchaseDao.getAllPurchases()

    fun getPurchasesForProduct(productId: Long): LiveData<List<Purchase>> = purchaseDao.getPurchasesForProduct(productId)

    @WorkerThread
    suspend fun insert(purchase: Purchase): Long = purchaseDao.insert(purchase)

    @WorkerThread
    suspend fun update(purchase: Purchase) = purchaseDao.update(purchase)

    @WorkerThread
    suspend fun delete(purchase: Purchase) = purchaseDao.delete(purchase)
}