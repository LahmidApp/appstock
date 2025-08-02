package com.example.appstock.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SaleItemDao {
    @Query("SELECT * FROM sale_items WHERE sale_id = :saleId")
    fun getItemsForSale(saleId: Long): LiveData<List<SaleItem>>
    
    @Query("SELECT * FROM sale_items WHERE sale_id = :saleId")
    suspend fun getItemsForSaleSync(saleId: Long): List<SaleItem>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(saleItem: SaleItem): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(saleItems: List<SaleItem>)
    
    @Update
    suspend fun update(saleItem: SaleItem)
    
    @Delete
    suspend fun delete(saleItem: SaleItem)
    
    @Query("DELETE FROM sale_items WHERE sale_id = :saleId")
    suspend fun deleteItemsForSale(saleId: Long)
}
