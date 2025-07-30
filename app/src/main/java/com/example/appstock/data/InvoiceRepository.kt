package com.example.appstock.data

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData

/**
 * Repository for accessing invoice data and associated items.
 */
class InvoiceRepository(
    private val invoiceDao: InvoiceDao,
    private val invoiceItemDao: InvoiceItemDao
) {
    val allInvoices: LiveData<List<Invoice>> = invoiceDao.getAllInvoices()

    @WorkerThread
    suspend fun getInvoiceById(id: Long): Invoice? = invoiceDao.getInvoiceById(id)

    @WorkerThread
    suspend fun insert(invoice: Invoice): Long = invoiceDao.insert(invoice)

    @WorkerThread
    suspend fun update(invoice: Invoice) = invoiceDao.update(invoice)

    @WorkerThread
    suspend fun delete(invoice: Invoice) = invoiceDao.delete(invoice)

    @WorkerThread
    suspend fun insertItem(item: InvoiceItem): Long = invoiceItemDao.insert(item)

    fun getItemsForInvoice(invoiceId: Long): LiveData<List<InvoiceItem>> = invoiceItemDao.getItemsForInvoice(invoiceId)

    @WorkerThread
    suspend fun updateItem(item: InvoiceItem) = invoiceItemDao.update(item)

    @WorkerThread
    suspend fun deleteItem(item: InvoiceItem) = invoiceItemDao.delete(item)
}