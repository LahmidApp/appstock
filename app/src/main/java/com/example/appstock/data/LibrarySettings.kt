package com.example.appstock.data

import java.math.BigDecimal

/**
 * Library settings entity for the library application
 */
data class LibrarySettings(
    val id: Long = 0,
    val name: String,
    val logoPath: String? = null,
    val address: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val taxRate: BigDecimal = BigDecimal.ZERO,
    val currency: String = "EUR"
)

