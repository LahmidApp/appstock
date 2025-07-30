package com.example.appstock.data

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData

/**
 * Repository for [Expense] data.
 */
class ExpenseRepository(private val expenseDao: ExpenseDao) {
    val allExpenses: LiveData<List<Expense>> = expenseDao.getAllExpenses()

    @WorkerThread
    suspend fun insert(expense: Expense): Long = expenseDao.insert(expense)

    @WorkerThread
    suspend fun update(expense: Expense) = expenseDao.update(expense)

    @WorkerThread
    suspend fun delete(expense: Expense) = expenseDao.delete(expense)
}