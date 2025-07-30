package com.example.appstock.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Entity representing a line item on a [Quote]. Each line item references a product
 * and specifies the quantity and pricing for that product on the quote.
 */
@Entity(
    tableName = "quote_items",
    foreignKeys = [
        ForeignKey(
            entity = Quote::class,
            parentColumns = ["id"],
            childColumns = ["quote_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Product::class,
            parentColumns = ["id"],
            childColumns = ["product_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ]
)
data class QuoteItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "quote_id", index = true) val quoteId: Long,
    @ColumnInfo(name = "product_id", index = true) val productId: Long,
    @ColumnInfo(name = "quantity") val quantity: Int,
    @ColumnInfo(name = "unit_price") val unitPrice: Double,
    @ColumnInfo(name = "total_price") val totalPrice: Double
)