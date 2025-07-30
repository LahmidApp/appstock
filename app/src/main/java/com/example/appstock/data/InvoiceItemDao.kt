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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: InvoiceItem): Long

    @Update
    suspend fun update(item: InvoiceItem)

    @Delete
    suspend fun delete(item: InvoiceItem)
}