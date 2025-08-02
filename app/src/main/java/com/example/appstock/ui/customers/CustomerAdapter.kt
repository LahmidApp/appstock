package com.example.appstock.ui.customers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.appstock.R
import com.example.appstock.data.Customer

class CustomerAdapter(private val onItemClick: (Customer) -> Unit) :
    ListAdapter<Customer, CustomerAdapter.CustomerViewHolder>(CustomerDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_customer, parent, false)
        return CustomerViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        val customer = getItem(position)
        holder.bind(customer)
        holder.itemView.setOnClickListener { onItemClick(customer) }
    }

    class CustomerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameText: TextView = itemView.findViewById(R.id.text_customer_name)
        private val phoneText: TextView = itemView.findViewById(R.id.text_customer_phone)
        fun bind(customer: Customer) {
            nameText.text = customer.name
            phoneText.text = customer.phoneNumber ?: ""
        }
    }

    class CustomerDiffCallback : DiffUtil.ItemCallback<Customer>() {
        override fun areItemsTheSame(oldItem: Customer, newItem: Customer): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Customer, newItem: Customer): Boolean = oldItem == newItem
    }
}
