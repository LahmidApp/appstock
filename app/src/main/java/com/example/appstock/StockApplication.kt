package com.example.appstock

import android.app.Application
import com.example.appstock.data.*

/**
 * Application class that initializes the database and repositories
 */
class StockApplication : Application() {
    
    // Database instance
    val database by lazy { AppDatabase.getDatabase(this) }
    
    // Repositories
    val productRepository by lazy { ProductRepository(database.productDao()) }
    val customerRepository by lazy { CustomerRepository(database.customerDao()) }
    val saleRepository by lazy { SaleRepository(database.saleDao()) }
    val saleHeaderRepository by lazy { SaleHeaderRepository(database.saleHeaderDao(), database.saleItemDao()) }
    val invoiceRepository by lazy { InvoiceRepository(database.invoiceDao(), database.invoiceItemDao()) }
    val purchaseRepository by lazy { PurchaseRepository(database.purchaseDao()) }
    val quoteRepository by lazy { QuoteRepository(database.quoteDao(), database.quoteItemDao()) }
    val expenseRepository by lazy { ExpenseRepository(database.expenseDao()) }
    val companyInfoRepository by lazy { CompanyInfoRepository(database.companyInfoDao()) }
    
    override fun onCreate() {
        super.onCreate()
    }
}
