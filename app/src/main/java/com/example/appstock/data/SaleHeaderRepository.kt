package com.example.appstock.data

import androidx.lifecycle.LiveData
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

/**
 * Repository for [SaleHeader] operations
 */
class SaleHeaderRepository(private val saleHeaderDao: SaleHeaderDao, private val saleItemDao: SaleItemDao) {
    
    val allSaleHeaders: LiveData<List<SaleHeader>> = saleHeaderDao.getAllSaleHeaders()

    fun getSaleItemsForSale(saleId: Long): LiveData<List<SaleItem>> = 
        saleItemDao.getItemsForSale(saleId)

    @Transaction
    suspend fun insertSaleWithItems(saleHeader: SaleHeader, saleItems: List<SaleItem>) {
        val saleHeaderId = saleHeaderDao.insert(saleHeader)
        val itemsWithHeaderId = saleItems.map { it.copy(saleId = saleHeaderId) }
        saleItemDao.insertAll(itemsWithHeaderId)
    }

    suspend fun insert(saleHeader: SaleHeader): Long = saleHeaderDao.insert(saleHeader)

    suspend fun update(saleHeader: SaleHeader) = saleHeaderDao.update(saleHeader)

    suspend fun delete(saleHeader: SaleHeader) = saleHeaderDao.delete(saleHeader)
    
    suspend fun updatePaymentStatus(saleId: Long, isPaid: Boolean) = 
        saleHeaderDao.updatePaymentStatus(saleId, isPaid)
}
