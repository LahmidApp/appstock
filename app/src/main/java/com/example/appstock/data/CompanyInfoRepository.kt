package com.example.appstock.data

import androidx.lifecycle.LiveData

/**
 * Repository for company information operations
 */
class CompanyInfoRepository(private val companyInfoDao: CompanyInfoDao) {
    
    fun getCompanyInfo(): LiveData<CompanyInfo?> = companyInfoDao.getCompanyInfo()
    
    suspend fun getCompanyInfoSync(): CompanyInfo? = companyInfoDao.getCompanyInfoSync()
    
    suspend fun insert(companyInfo: CompanyInfo) = companyInfoDao.insert(companyInfo)
    
    suspend fun update(companyInfo: CompanyInfo) = companyInfoDao.update(companyInfo)
    
    suspend fun insertOrUpdate(companyInfo: CompanyInfo) {
        val existing = getCompanyInfoSync()
        if (existing == null) {
            insert(companyInfo)
        } else {
            update(companyInfo.copy(id = existing.id))
        }
    }
}
