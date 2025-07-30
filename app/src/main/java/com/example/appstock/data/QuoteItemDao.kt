package com.example.appstock.data

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * DAO for [QuoteItem] entities.
 */
@Dao
interface QuoteItemDao {
    @Query("SELECT * FROM quote_items WHERE quote_id = :quoteId")
    fun getItemsForQuote(quoteId: Long): LiveData<List<QuoteItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: QuoteItem): Long

    @Update
    suspend fun update(item: QuoteItem)

    @Delete
    suspend fun delete(item: QuoteItem)
}