package com.example.appstock.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Entity representing a quote (devis). A quote is similar to an invoice but may not be finalized.
 * It references a [Customer] and contains multiple [QuoteItem]s.
 */
@Entity(
    tableName = "quotes",
    foreignKeys = [
        ForeignKey(
            entity = Customer::class,
            parentColumns = ["id"],
            childColumns = ["customer_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Quote(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "customer_id", index = true) val customerId: Long,
    @ColumnInfo(name = "product_id", index = true) val productId: Long,
    @ColumnInfo(name = "quantity") val quantity: Int,
    @ColumnInfo(name = "unit_price") val unitPrice: Double,
    @ColumnInfo(name = "total_amount") val totalAmount: Double,
    @ColumnInfo(name = "description") val description: String = "",
    @ColumnInfo(name = "date_timestamp") val dateTimestamp: Long,
    @ColumnInfo(name = "status") val status: String = "pending"
)