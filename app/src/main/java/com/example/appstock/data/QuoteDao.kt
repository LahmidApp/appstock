package com.example.appstock.data

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * DAO for [Quote] entities.
 */
@Dao
interface QuoteDao {
    @Query("SELECT * FROM quotes ORDER BY date_timestamp DESC")
    fun getAllQuotes(): LiveData<List<Quote>>

    @Query("SELECT * FROM quotes WHERE id = :id")
    suspend fun getQuoteById(id: Long): Quote?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(quote: Quote): Long

    @Update
    suspend fun update(quote: Quote)

    @Delete
    suspend fun delete(quote: Quote)
}