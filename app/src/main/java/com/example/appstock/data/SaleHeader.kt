package com.example.appstock.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a sale transaction header.
 * Contains general information about the sale, while individual products are in SaleItem.
 */
@Entity(tableName = "sale_headers")
data class SaleHeader(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "customer_id", index = true) val customerId: Long? = null,
    @ColumnInfo(name = "total_amount") val totalAmount: Double,
    @ColumnInfo(name = "date_timestamp") val dateTimestamp: Long,
    @ColumnInfo(name = "sale_date") val saleDate: Long = dateTimestamp,
    @ColumnInfo(name = "notes") val notes: String? = null,
    @ColumnInfo(name = "is_paid") val isPaid: Boolean = false
)
