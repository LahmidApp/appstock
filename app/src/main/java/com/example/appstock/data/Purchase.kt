package com.example.appstock.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a purchase transaction, where stock is added. It tracks the product,
 * quantity purchased, purchase price, vendor information and the date of purchase.
 */
@Entity(tableName = "purchases")
data class Purchase(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "product_id", index = true) val productId: Long,
    @ColumnInfo(name = "quantity") val quantity: Int,
    @ColumnInfo(name = "unit_price") val unitPrice: Double,
    @ColumnInfo(name = "total_price") val totalPrice: Double,
    @ColumnInfo(name = "vendor") val vendor: String? = null,
    @ColumnInfo(name = "date_timestamp") val dateTimestamp: Long
)