package com.example.appstock.data

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * Data access object for performing CRUD operations on [Customer] entities.
 */
@Dao
interface CustomerDao {

    @Query("SELECT * FROM customers ORDER BY name")
    fun getAllCustomers(): LiveData<List<Customer>>

    // Récupère les N derniers clients ajoutés (par date de création)
    @Query("SELECT * FROM customers ORDER BY created_at DESC LIMIT :limit")
    fun getRecentCustomers(limit: Int = 10): LiveData<List<Customer>>

    // Récupère les clients les plus fidèles (ceux ayant le plus de ventes)
    @Query("SELECT c.* FROM customers c LEFT JOIN sales s ON c.id = s.customer_id GROUP BY c.id ORDER BY COUNT(s.id) DESC LIMIT :limit")
    fun getLoyalCustomers(limit: Int = 10): LiveData<List<Customer>>

    @Query("SELECT * FROM customers WHERE id = :id")
    suspend fun getCustomerById(id: Long): Customer?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(customer: Customer): Long

    @Update
    suspend fun update(customer: Customer)

    @Delete
    suspend fun delete(customer: Customer)
}