package com.example.appstock.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a sale transaction. Each sale records the product sold, quantity,
 * sale price, customer information (optional if linked to invoice) and date of sale.
 */
@Entity(tableName = "sales")
data class Sale(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "product_id", index = true) val productId: Long,
    @ColumnInfo(name = "quantity") val quantity: Int,
    @ColumnInfo(name = "unit_price") val unitPrice: Double,
    @ColumnInfo(name = "total_price") val totalPrice: Double,
    @ColumnInfo(name = "customer_id", index = true) val customerId: Long? = null,
    @ColumnInfo(name = "date_timestamp") val dateTimestamp: Long
)