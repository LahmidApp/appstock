package com.example.appstock.data

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * DAO for [InvoiceItem] entities. Provides CRUD operations to manage invoice line items.
 */
@Dao
interface InvoiceItemDao {
    @Query("SELECT * FROM invoice_items WHERE invoice_id = :invoiceId")
    fun getItemsForInvoice(invoiceId: Long): LiveData<List<InvoiceItem>>

    @Query("SELECT * FROM invoice_items WHERE invoice_id = :invoiceId")
    suspend fun getItemsForInvoiceSync(invoiceId: Long): List<InvoiceItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: InvoiceItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoiceItem(item: InvoiceItem): Long // Alias plus explicite

    @Update
    suspend fun update(item: InvoiceItem)

    @Delete
    suspend fun delete(item: InvoiceItem)
}