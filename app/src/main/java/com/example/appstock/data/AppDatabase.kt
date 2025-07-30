package com.example.appstock.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
// import androidx.room.TypeConverters // Supprimez cet import si la classe Converters est supprimée ou vide de convertisseurs utilisés
import com.example.appstock.data.ProductDao

@Database(
    entities = [Product::class],
    version = 1,
    exportSchema = false
)
// @TypeConverters(Converters::class) // SUPPRIMEZ ou COMMENTEZ cette ligne si Converters n'est plus nécessaire pour les entités de cette DB
abstract class AppDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao

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
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
    