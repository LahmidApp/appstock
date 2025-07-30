package com.example.appstock.data

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * DAO for [Purchase] entities.
 */
@Dao
interface PurchaseDao {
    @Query("SELECT * FROM purchases ORDER BY date_timestamp DESC")
    fun getAllPurchases(): LiveData<List<Purchase>>

    @Query("SELECT * FROM purchases WHERE product_id = :productId ORDER BY date_timestamp DESC")
    fun getPurchasesForProduct(productId: Long): LiveData<List<Purchase>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(purchase: Purchase): Long

    @Update
    suspend fun update(purchase: Purchase)

    @Delete
    suspend fun delete(purchase: Purchase)
}