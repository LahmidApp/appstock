package com.example.appstock.ui.sales


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appstock.R
import com.example.appstock.data.AppDatabase
import com.example.appstock.data.ProductRepository
import com.example.appstock.data.SaleRepository
import com.example.appstock.viewmodel.ProductViewModel
import com.example.appstock.viewmodel.ProductViewModelFactory
import com.example.appstock.viewmodel.SaleViewModel
import com.example.appstock.viewmodel.SaleViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton

class SalesFragment : Fragment() {
    private lateinit var saleViewModel: SaleViewModel
    private lateinit var productViewModel: ProductViewModel
    private lateinit var adapter: com.example.appstock.ui.sales.SaleAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_sales, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.sales_recycler_view)
        val fab = view.findViewById<FloatingActionButton>(R.id.fab_add_sale)

        val db = com.example.appstock.data.AppDatabase.getDatabase(requireContext())
        val saleRepo = SaleRepository(db.saleDao())
        val productRepo = ProductRepository(db.productDao())
        saleViewModel = ViewModelProvider(this, SaleViewModelFactory(saleRepo))[SaleViewModel::class.java]
        productViewModel = ViewModelProvider(this, ProductViewModelFactory(productRepo))[ProductViewModel::class.java]

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        saleViewModel.allSales.observe(viewLifecycleOwner) { sales ->
            productViewModel.allProducts.observe(viewLifecycleOwner) { products ->
                val productMap = products.associateBy { it.id }
                adapter = com.example.appstock.ui.sales.SaleAdapter(sales, productMap)
                recyclerView.adapter = adapter
            }
        }

        fab.setOnClickListener {
            startActivity(Intent(requireContext(), com.example.appstock.ui.sales.AddEditSaleActivity::class.java))
        }

        return view
    }
}