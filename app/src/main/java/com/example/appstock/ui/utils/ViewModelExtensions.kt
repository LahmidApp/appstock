package com.example.appstock.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appstock.StockApplication
import com.example.appstock.viewmodel.*

/**
 * Extension functions to get ViewModels with their factories
 */

@Composable
fun getCustomerViewModel(): CustomerViewModel {
    val application = LocalContext.current.applicationContext as StockApplication
    return viewModel(factory = CustomerViewModelFactory(application.customerRepository))
}

@Composable
fun getSaleViewModel(): SaleViewModel {
    val application = LocalContext.current.applicationContext as StockApplication
    return viewModel(factory = SaleViewModelFactory(application.saleRepository))
}

@Composable
fun getSaleHeaderViewModel(): SaleHeaderViewModel {
    val application = LocalContext.current.applicationContext as StockApplication
    return viewModel(factory = SaleHeaderViewModelFactory(application.saleHeaderRepository))
}

@Composable
fun getInvoiceViewModel(): InvoiceViewModel {
    val application = LocalContext.current.applicationContext as StockApplication
    return viewModel(factory = InvoiceViewModelFactory(application.invoiceRepository))
}

@Composable
fun getProductViewModel(): ProductViewModel {
    val application = LocalContext.current.applicationContext as StockApplication
    return viewModel(factory = ProductViewModelFactory(application.productRepository))
}

@Composable
fun getPurchaseViewModel(): PurchaseViewModel {
    val application = LocalContext.current.applicationContext as StockApplication
    return viewModel(factory = PurchaseViewModelFactory(application.purchaseRepository))
}

@Composable
fun getQuoteViewModel(): QuoteViewModel {
    val application = LocalContext.current.applicationContext as StockApplication
    return viewModel(factory = QuoteViewModelFactory(application.quoteRepository))
}

@Composable
fun getExpenseViewModel(): ExpenseViewModel {
    val application = LocalContext.current.applicationContext as StockApplication
    return viewModel(factory = ExpenseViewModelFactory(application.expenseRepository))
}

@Composable
fun getCompanyInfoViewModel(): CompanyInfoViewModel {
    val application = LocalContext.current.applicationContext as StockApplication
    return viewModel(factory = CompanyInfoViewModelFactory(application.companyInfoRepository))
}
