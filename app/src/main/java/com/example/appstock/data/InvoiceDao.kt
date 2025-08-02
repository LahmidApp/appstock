package com.example.appstock.data

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * DAO for accessing [Invoice] entities. Provides methods to insert, update and delete
 * invoices and to retrieve invoices along with their items and associated customer names.
 */
@Dao
interface InvoiceDao {

    @Query("SELECT * FROM invoices ORDER BY date_timestamp DESC")
    fun getAllInvoices(): LiveData<List<Invoice>>

    @Query("SELECT * FROM invoices WHERE is_paid = 0 ORDER BY date_timestamp DESC")
    fun getPendingInvoices(): LiveData<List<Invoice>>

    @Query("SELECT * FROM invoices WHERE id = :id")
    suspend fun getInvoiceById(id: Long): Invoice?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(invoice: Invoice): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoice(invoice: Invoice): Long // Alias plus explicite

    @Update
    suspend fun update(invoice: Invoice)

    @Delete
    suspend fun delete(invoice: Invoice)
}