package com.example.appstock.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Entity representing an invoice. An invoice is issued to a [customer] and contains
 * multiple [InvoiceItem]s. Use [InvoiceItem] to store the individual line items.
 *
 * @property id the primary key of the invoice
 * @property customerId foreign key referencing a [Customer]
 * @property dateTimestamp time of invoice creation in milliseconds since epoch
 * @property totalAmount sum of all line item totals; maintained by application logic
 * @property isPaid whether the invoice has been paid
 */
@Entity(
    tableName = "invoices",
    foreignKeys = [
        ForeignKey(
            entity = Customer::class,
            parentColumns = ["id"],
            childColumns = ["customer_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Invoice(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "customer_id", index = true) val customerId: Long,
    @ColumnInfo(name = "date_timestamp") val dateTimestamp: Long,
    @ColumnInfo(name = "total_amount") val totalAmount: Double,
    @ColumnInfo(name = "is_paid") val isPaid: Boolean = false
)