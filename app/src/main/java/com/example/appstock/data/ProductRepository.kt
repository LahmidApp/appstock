package com.example.appstock.data

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData

/**
 * Repository providing an abstraction layer over [ProductDao] and making it easy to test.
 */
class ProductRepository(private val productDao: ProductDao) {
    /** Live list of all products sorted by name. */
    val allProducts: LiveData<List<Product>> = productDao.getAllProducts()

    @WorkerThread
    suspend fun getProductById(id: Long): Product? = productDao.getProductById(id)

    @WorkerThread
    suspend fun insert(product: Product): Long = productDao.insert(product)

    @WorkerThread
    suspend fun update(product: Product) = productDao.update(product)

    @WorkerThread
    suspend fun delete(product: Product) = productDao.delete(product)
}