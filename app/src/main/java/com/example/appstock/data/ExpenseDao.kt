package com.example.appstock.data

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * DAO for [Expense] entities.
 */
@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses ORDER BY date_timestamp DESC")
    fun getAllExpenses(): LiveData<List<Expense>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expense: Expense): Long

    @Update
    suspend fun update(expense: Expense)

    @Delete
    suspend fun delete(expense: Expense)
}