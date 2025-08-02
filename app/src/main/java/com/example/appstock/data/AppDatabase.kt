package com.example.appstock.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Product::class,
        Customer::class,
        Sale::class,
        SaleHeader::class,
        SaleItem::class,
        Invoice::class,
        InvoiceItem::class,
        Purchase::class,
        Quote::class,
        QuoteItem::class,
        Expense::class,
        CompanyInfo::class
    ],
    version = 11, // Incrémenté pour le nouveau champ ICE dans Customer
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
    abstract fun customerDao(): CustomerDao
    abstract fun saleDao(): SaleDao
    abstract fun saleHeaderDao(): SaleHeaderDao
    abstract fun saleItemDao(): SaleItemDao
    abstract fun invoiceDao(): InvoiceDao
    abstract fun invoiceItemDao(): InvoiceItemDao
    abstract fun purchaseDao(): PurchaseDao
    abstract fun quoteDao(): QuoteDao
    abstract fun quoteItemDao(): QuoteItemDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun companyInfoDao(): CompanyInfoDao

    companion object {
        // ... (le reste du code du companion object reste le même) ...
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "appstock_database"
                )
                    .fallbackToDestructiveMigration() // Pour la migration de version 1 à 2
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Ajouter des données de test
                            CoroutineScope(Dispatchers.IO).launch {
                                populateDatabase(INSTANCE!!)
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }
        
        private suspend fun populateDatabase(database: AppDatabase) {
            val productDao = database.productDao()
            val customerDao = database.customerDao()
            
            // Ajouter quelques produits de test
            val sampleProducts = listOf(
                Product(
                    name = "Téléphone Samsung",
                    price = 299.99,
                    costPrice = 200.0,
                    stock = 25,
                    barcode = "1234567890123",
                    qrCode = "QR123",
                    description = "Smartphone Samsung Galaxy",
                    types = listOf("Électronique", "Téléphone"),
                    supplier = "Samsung",
                    minStockLevel = 5
                ),
                Product(
                    name = "Ordinateur portable",
                    price = 899.99,
                    costPrice = 650.0,
                    stock = 10,
                    barcode = "1234567890124",
                    qrCode = "QR124",
                    description = "Laptop HP 15 pouces",
                    types = listOf("Électronique", "Ordinateur"),
                    supplier = "HP",
                    minStockLevel = 2
                ),
                Product(
                    name = "Chaise de bureau",
                    price = 149.99,
                    costPrice = 80.0,
                    stock = 15,
                    barcode = "1234567890125",
                    qrCode = "QR125",
                    description = "Chaise ergonomique",
                    types = listOf("Mobilier", "Bureau"),
                    supplier = "IKEA",
                    minStockLevel = 3
                )
            )
            
            sampleProducts.forEach { product ->
                productDao.insert(product)
            }
            
            // Ajouter quelques clients de test
            val sampleCustomers = listOf(
                Customer(
                    name = "Ihocine",
                    email = "ihocine@example.com",
                    phoneNumber = "+212 6 12 34 56 78",
                    address = "123 Rue Mohammed V, Casablanca",
                    ice = "123456789012345",
                    createdAt = System.currentTimeMillis()
                ),
                Customer(
                    name = "Ahmed Benjelloun",
                    email = "ahmed.benjelloun@example.com",
                    phoneNumber = "+212 6 87 65 43 21",
                    address = "456 Avenue Hassan II, Rabat",
                    ice = "987654321098765",
                    createdAt = System.currentTimeMillis()
                ),
                Customer(
                    name = "Fatima El Kadiri",
                    email = "fatima.elkadiri@example.com",
                    phoneNumber = "+212 6 11 22 33 44",
                    address = "789 Boulevard Zerktouni, Marrakech",
                    ice = null, // Client particulier sans ICE
                    createdAt = System.currentTimeMillis()
                )
            )
            
            sampleCustomers.forEach { customer ->
                customerDao.insert(customer)
            }
        }
    }
}
    