package com.example.appstock.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SaleHeaderDao {
    @Query("SELECT * FROM sale_headers ORDER BY date_timestamp DESC")
    fun getAllSaleHeaders(): LiveData<List<SaleHeader>>
    
    @Query("SELECT * FROM sale_headers WHERE id = :id")
    suspend fun getSaleHeaderById(id: Long): SaleHeader?
    
    @Query("SELECT * FROM sale_headers WHERE customer_id = :customerId ORDER BY date_timestamp DESC")
    fun getSaleHeadersByCustomer(customerId: Long): LiveData<List<SaleHeader>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(saleHeader: SaleHeader): Long
    
    @Update
    suspend fun update(saleHeader: SaleHeader)
    
    @Delete
    suspend fun delete(saleHeader: SaleHeader)
    
    @Query("SELECT SUM(total_amount) FROM sale_headers WHERE date_timestamp BETWEEN :startDate AND :endDate")
    suspend fun getTotalSalesBetweenDates(startDate: Long, endDate: Long): Double?
    
    @Query("SELECT COUNT(*) FROM sale_headers WHERE date_timestamp BETWEEN :startDate AND :endDate")
    suspend fun getSalesCountBetweenDates(startDate: Long, endDate: Long): Int
    
    @Query("UPDATE sale_headers SET is_paid = :isPaid WHERE id = :saleId")
    suspend fun updatePaymentStatus(saleId: Long, isPaid: Boolean)
}
