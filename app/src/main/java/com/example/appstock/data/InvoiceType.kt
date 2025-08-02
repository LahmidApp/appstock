package com.example.appstock.data

/**
 * Types de documents pour la facturation
 */
enum class InvoiceType(val displayName: String, val fileName: String) {
    FACTURE("Facture", "Facture"),
    BON_LIVRAISON("Bon de Livraison", "Bon_Livraison");
    
    companion object {
        fun fromString(value: String): InvoiceType {
            return when (value) {
                "BON_LIVRAISON" -> BON_LIVRAISON
                else -> FACTURE
            }
        }
    }
}
