package com.example.appstock.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing a customer (client) of the library. Clients are used in invoices and quotes.
 *
 * Each customer has a unique [id], a [name], and optional contact information such as
 * [phoneNumber], [email], [address] and [ice] (Identifiant Commun de l'Entreprise).
 */
@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "phone_number") val phoneNumber: String? = null,
    @ColumnInfo(name = "email") val email: String? = null,
    @ColumnInfo(name = "address") val address: String? = null,
    @ColumnInfo(name = "ice") val ice: String? = null, // Identifiant Commun de l'Entreprise (ICE)
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)