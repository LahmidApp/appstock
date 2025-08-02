package com.example.appstock.data

import androidx.lifecycle.LiveData
import androidx.room.*

/**
 * DAO for company information
 */
@Dao
interface CompanyInfoDao {
    @Query("SELECT * FROM company_info WHERE id = 1")
    fun getCompanyInfo(): LiveData<CompanyInfo?>
    
    @Query("SELECT * FROM company_info WHERE id = 1")
    suspend fun getCompanyInfoSync(): CompanyInfo?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(companyInfo: CompanyInfo)
    
    @Update
    suspend fun update(companyInfo: CompanyInfo)
}
