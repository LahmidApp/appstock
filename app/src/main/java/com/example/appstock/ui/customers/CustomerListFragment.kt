package com.example.appstock.ui.customers

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appstock.R
import com.example.appstock.data.CustomerRepository
import com.example.appstock.data.AppDatabase
import com.example.appstock.viewmodel.CustomerViewModel
import com.example.appstock.viewmodel.CustomerViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CustomerListFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CustomerAdapter
    private lateinit var customerViewModel: CustomerViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_customer_list, container, false)
        recyclerView = view.findViewById(R.id.recycler_view_customers)
        val fab: FloatingActionButton = view.findViewById(R.id.fab_add_customer)

        adapter = CustomerAdapter { customer ->
            val intent = Intent(requireContext(), AddEditCustomerActivity::class.java)
            intent.putExtra(AddEditCustomerActivity.EXTRA_CUSTOMER_ID, customer.id)
            startActivity(intent)
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))

        fab.setOnClickListener {
            val intent = Intent(requireContext(), AddEditCustomerActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val database = AppDatabase.getDatabase(requireContext())
        val repository = CustomerRepository(database.customerDao())
        val factory = CustomerViewModelFactory(repository)
        customerViewModel = ViewModelProvider(this, factory)[CustomerViewModel::class.java]
        customerViewModel.allCustomers.observe(viewLifecycleOwner) { customers ->
            adapter.submitList(customers)
        }
    }
}
