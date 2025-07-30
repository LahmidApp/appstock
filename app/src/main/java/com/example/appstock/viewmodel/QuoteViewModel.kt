package com.example.appstock.viewmodel

import androidx.lifecycle.*
import com.example.appstock.data.Quote
import com.example.appstock.data.QuoteItem
import com.example.appstock.data.QuoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ViewModel for [Quote] and [QuoteItem] operations.
 */
class QuoteViewModel(private val repository: QuoteRepository) : ViewModel() {
    val allQuotes: LiveData<List<Quote>> = repository.allQuotes

    fun insert(quote: Quote) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(quote)
    }

    fun update(quote: Quote) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(quote)
    }

    fun delete(quote: Quote) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(quote)
    }

    fun getItemsForQuote(quoteId: Long): LiveData<List<QuoteItem>> = repository.getItemsForQuote(quoteId)

    fun insertItem(item: QuoteItem) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertItem(item)
    }

    fun updateItem(item: QuoteItem) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateItem(item)
    }

    fun deleteItem(item: QuoteItem) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteItem(item)
    }
}

class QuoteViewModelFactory(private val repository: QuoteRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return QuoteViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}