package com.example.appstock.data

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData

/**
 * Repository for accessing quotes and their items.
 */
class QuoteRepository(
    private val quoteDao: QuoteDao,
    private val quoteItemDao: QuoteItemDao
) {
    val allQuotes: LiveData<List<Quote>> = quoteDao.getAllQuotes()

    @WorkerThread
    suspend fun getQuoteById(id: Long): Quote? = quoteDao.getQuoteById(id)

    @WorkerThread
    suspend fun insert(quote: Quote): Long = quoteDao.insert(quote)

    @WorkerThread
    suspend fun update(quote: Quote) = quoteDao.update(quote)

    @WorkerThread
    suspend fun delete(quote: Quote) = quoteDao.delete(quote)

    fun getItemsForQuote(quoteId: Long): LiveData<List<QuoteItem>> = quoteItemDao.getItemsForQuote(quoteId)

    @WorkerThread
    suspend fun insertItem(item: QuoteItem): Long = quoteItemDao.insert(item)

    @WorkerThread
    suspend fun updateItem(item: QuoteItem) = quoteItemDao.update(item)

    @WorkerThread
    suspend fun deleteItem(item: QuoteItem) = quoteItemDao.delete(item)
}