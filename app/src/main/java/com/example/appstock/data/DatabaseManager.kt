/*package com.example.appstock.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

/**
 * Database manager for PostgreSQL connection and operations
 */
class DatabaseManager {
    companion object {
        private const val DATABASE_URL = "jdbc:postgresql://ep-proud-field-ae0jdi0j-pooler.c-2.us-east-2.aws.neon.tech/appstock?user=neondb_owner&password=npg_LC7Gx5PZMzES&sslmode=require&channelBinding=require"
        
        @Volatile
        private var INSTANCE: DatabaseManager? = null
        
        fun getInstance(): DatabaseManager {
            return INSTANCE ?: synchronized(this) {
                val instance = DatabaseManager()
                INSTANCE = instance
                instance
            }
        }
    }
    
    private suspend fun getConnection(): Connection = withContext(Dispatchers.IO) {
        try {
            Class.forName("org.postgresql.Driver")
            DriverManager.getConnection(DATABASE_URL)
        } catch (e: Exception) {
            throw SQLException("Failed to connect to database: ${e.message}", e)
        }
    }
    
    suspend fun initializeDatabase() = withContext(Dispatchers.IO) {
        val connection = getConnection()
        try {
            // Create tables if they don't exist
            createTables(connection)
        } finally {
            connection.close()
        }
    }
    
    private fun createTables(connection: Connection) {
        val statements = listOf(
            """
            CREATE TABLE IF NOT EXISTS products (
                id SERIAL PRIMARY KEY,
                name VARCHAR(255) NOT NULL,
                description TEXT,
                price DECIMAL(10,2) NOT NULL,
                cost_price DECIMAL(10,2) NOT NULL,
                quantity INTEGER NOT NULL DEFAULT 0,
                barcode VARCHAR(255) UNIQUE,
                qr_code VARCHAR(255) UNIQUE,
                category VARCHAR(255),
                supplier VARCHAR(255),
                min_stock_level INTEGER DEFAULT 0,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS customers (
                id SERIAL PRIMARY KEY,
                name VARCHAR(255) NOT NULL,
                email VARCHAR(255),
                phone VARCHAR(50),
                address TEXT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS invoices (
                id SERIAL PRIMARY KEY,
                invoice_number VARCHAR(100) UNIQUE NOT NULL,
                customer_id INTEGER REFERENCES customers(id),
                total_amount DECIMAL(10,2) NOT NULL,
                tax_amount DECIMAL(10,2) DEFAULT 0,
                discount_amount DECIMAL(10,2) DEFAULT 0,
                status VARCHAR(50) DEFAULT 'PENDING',
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                due_date TIMESTAMP
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS invoice_items (
                id SERIAL PRIMARY KEY,
                invoice_id INTEGER REFERENCES invoices(id) ON DELETE CASCADE,
                product_id INTEGER REFERENCES products(id),
                quantity INTEGER NOT NULL,
                unit_price DECIMAL(10,2) NOT NULL,
                total_price DECIMAL(10,2) NOT NULL
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS quotes (
                id SERIAL PRIMARY KEY,
                quote_number VARCHAR(100) UNIQUE NOT NULL,
                customer_id INTEGER REFERENCES customers(id),
                total_amount DECIMAL(10,2) NOT NULL,
                tax_amount DECIMAL(10,2) DEFAULT 0,
                discount_amount DECIMAL(10,2) DEFAULT 0,
                status VARCHAR(50) DEFAULT 'PENDING',
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                valid_until TIMESTAMP
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS quote_items (
                id SERIAL PRIMARY KEY,
                quote_id INTEGER REFERENCES quotes(id) ON DELETE CASCADE,
                product_id INTEGER REFERENCES products(id),
                quantity INTEGER NOT NULL,
                unit_price DECIMAL(10,2) NOT NULL,
                total_price DECIMAL(10,2) NOT NULL
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS expenses (
                id SERIAL PRIMARY KEY,
                description VARCHAR(255) NOT NULL,
                amount DECIMAL(10,2) NOT NULL,
                category VARCHAR(255),
                date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                receipt_path VARCHAR(500)
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS purchases (
                id SERIAL PRIMARY KEY,
                supplier VARCHAR(255) NOT NULL,
                total_amount DECIMAL(10,2) NOT NULL,
                status VARCHAR(50) DEFAULT 'PENDING',
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                received_at TIMESTAMP
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS sales (
                id SERIAL PRIMARY KEY,
                customer_id INTEGER REFERENCES customers(id),
                total_amount DECIMAL(10,2) NOT NULL,
                payment_method VARCHAR(50),
                status VARCHAR(50) DEFAULT 'COMPLETED',
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """,
            """
            CREATE TABLE IF NOT EXISTS library_settings (
                id SERIAL PRIMARY KEY,
                name VARCHAR(255) NOT NULL,
                logo_path VARCHAR(500),
                address TEXT,
                phone VARCHAR(50),
                email VARCHAR(255),
                tax_rate DECIMAL(5,2) DEFAULT 0,
                currency VARCHAR(10) DEFAULT 'USD'
            )
            """
        )
        
        statements.forEach { sql ->
            connection.createStatement().use { statement ->
                statement.execute(sql)
            }
        }
    }
    
    suspend fun executeQuery(sql: String, params: List<Any> = emptyList()): ResultSet? = withContext(Dispatchers.IO) {
        val connection = getConnection()
        val statement = connection.prepareStatement(sql)
        params.forEachIndexed { index, param ->
            statement.setObject(index + 1, param)
        }
        statement.executeQuery()
    }
    
    suspend fun executeUpdate(sql: String, params: List<Any> = emptyList()): Int = withContext(Dispatchers.IO) {
        val connection = getConnection()
        try {
            val statement = connection.prepareStatement(sql)
            params.forEachIndexed { index, param ->
                statement.setObject(index + 1, param)
            }
            statement.executeUpdate()
        } finally {
            connection.close()
        }
    }
    
    suspend fun executeInsert(sql: String, params: List<Any> = emptyList()): Long = withContext(Dispatchers.IO) {
        val connection = getConnection()
        try {
            val statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)
            params.forEachIndexed { index, param ->
                statement.setObject(index + 1, param)
            }
            statement.executeUpdate()
            
            val generatedKeys = statement.generatedKeys
            if (generatedKeys.next()) {
                generatedKeys.getLong(1)
            } else {
                throw SQLException("Creating record failed, no ID obtained.")
            }
        } finally {
            connection.close()
        }
    }
}

*/