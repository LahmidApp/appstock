package com.example.appstock.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing an expense record for the library. Expenses track outflows
 * of money, such as rent, utilities, salaries or other operating costs.
 */
@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "category") val category: String,
    @ColumnInfo(name = "amount") val amount: Double,
    @ColumnInfo(name = "date_timestamp") val dateTimestamp: Long,
    @ColumnInfo(name = "description") val description: String? = null
)