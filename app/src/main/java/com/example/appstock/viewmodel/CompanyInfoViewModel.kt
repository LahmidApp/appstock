package com.example.appstock.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appstock.data.CompanyInfo
import com.example.appstock.data.CompanyInfoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ViewModel for company information management
 */
class CompanyInfoViewModel(private val repository: CompanyInfoRepository) : ViewModel() {
    
    val companyInfo: LiveData<CompanyInfo?> = repository.getCompanyInfo()
    
    fun insertOrUpdate(companyInfo: CompanyInfo) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertOrUpdate(companyInfo)
    }
    
    fun insert(companyInfo: CompanyInfo) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(companyInfo)
    }
    
    fun update(companyInfo: CompanyInfo) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(companyInfo)
    }
}
