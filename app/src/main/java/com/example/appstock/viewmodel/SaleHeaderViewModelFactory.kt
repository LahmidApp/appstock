package com.example.appstock.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.appstock.data.SaleHeaderRepository

class SaleHeaderViewModelFactory(private val repository: SaleHeaderRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SaleHeaderViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SaleHeaderViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
