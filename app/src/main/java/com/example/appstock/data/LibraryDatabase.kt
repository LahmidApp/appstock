package com.example.appstock.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Room database for the library application. It hosts all the entities and provides
 * DAOs to access them. Ensure the version is incremented whenever the schema changes.
 */
@Database(
    entities = [
        Product::class,
        Customer::class,
        Invoice::class,
        InvoiceItem::class,
        Quote::class,
        QuoteItem::class,
        Expense::class,
        Purchase::class,
        Sale::class
    ],
    version = 1,
    exportSchema = false
)
abstract class LibraryDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun customerDao(): CustomerDao
    abstract fun invoiceDao(): InvoiceDao
    abstract fun invoiceItemDao(): InvoiceItemDao
    abstract fun quoteDao(): QuoteDao
    abstract fun quoteItemDao(): QuoteItemDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun purchaseDao(): PurchaseDao
    abstract fun saleDao(): SaleDao

    companion object {
        @Volatile
        private var INSTANCE: LibraryDatabase? = null

        /**
         * Returns the singleton instance of [LibraryDatabase]. If the database
         * doesn't exist yet it will be created.
         */
        fun getDatabase(context: Context): LibraryDatabase {
            android.util.Log.d("DB_PATH", context.getDatabasePath("library_database").absolutePath)
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LibraryDatabase::class.java,
                    "library_database"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}