package com.example.appstock.data

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * DAO for [Sale] entities.
 */
@Dao
interface SaleDao {
    @Query("SELECT * FROM sales ORDER BY date_timestamp DESC")
    fun getAllSales(): LiveData<List<Sale>>

    @Query("SELECT * FROM sales WHERE product_id = :productId ORDER BY date_timestamp DESC")
    fun getSalesForProduct(productId: Long): LiveData<List<Sale>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(sale: Sale): Long

    @Update
    suspend fun update(sale: Sale)

    @Delete
    suspend fun delete(sale: Sale)
}