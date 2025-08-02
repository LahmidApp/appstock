package com.example.appstock.ui.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.appstock.data.CompanyInfoRepository
import com.example.appstock.viewmodel.CompanyInfoViewModel

/**
 * Factory for creating CompanyInfoViewModel instances
 */
class CompanyInfoViewModelFactory(private val repository: CompanyInfoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CompanyInfoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CompanyInfoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
