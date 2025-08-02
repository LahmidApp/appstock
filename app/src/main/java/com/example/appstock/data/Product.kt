package com.example.appstock.data

import androidx.room.Entity

import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.TypeConverters
import com.example.appstock.data.converters.StringListConverter

/**
 * Entity representing a product in the library inventory.
 */
@Entity(tableName = "products")
@TypeConverters(StringListConverter::class)
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "price") val price: Double,
    @ColumnInfo(name = "costPrice") val costPrice: Double,
    @ColumnInfo(name = "stock") val stock: Int,
    @ColumnInfo(name = "barcode") val barcode: String,
    @ColumnInfo(name = "types") val types: List<String> = emptyList(),
    @ColumnInfo(name = "supplier") val supplier: String?,
    @ColumnInfo(name = "minStockLevel") val minStockLevel: Int?,
    @ColumnInfo(name = "qr_code") val qrCode: String,
    @ColumnInfo(name = "description") val description: String? = null,
    @ColumnInfo(name = "photo_uri") val photoUri: String? = null
)