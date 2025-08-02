package com.example.appstock.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity representing company information for invoices and quotes
 */
@Entity(tableName = "company_info")
data class CompanyInfo(
    @PrimaryKey val id: Long = 1, // Single record
    @ColumnInfo(name = "company_name") val companyName: String,
    @ColumnInfo(name = "address") val address: String,
    @ColumnInfo(name = "phone") val phone: String,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "website") val website: String? = null,
    @ColumnInfo(name = "logo_uri") val logoUri: String? = null,
    @ColumnInfo(name = "ice") val ice: String? = null, // Identifiant Commun de l'Entreprise
    @ColumnInfo(name = "if_number") val ifNumber: String? = null, // Identifiant Fiscal
    @ColumnInfo(name = "patente") val patente: String? = null,
    @ColumnInfo(name = "rc") val rc: String? = null, // Registre de Commerce
    @ColumnInfo(name = "cnss") val cnss: String? = null,
    @ColumnInfo(name = "bank_account") val bankAccount: String? = null,
    @ColumnInfo(name = "currency") val currency: String = "MAD",
    @ColumnInfo(name = "invoice_type") val invoiceType: String = "FACTURE", // "FACTURE" ou "BON_LIVRAISON"
    @ColumnInfo(name = "tva_rate") val tvaRate: Double = 20.0 // Taux de TVA par défaut (20%)
)
