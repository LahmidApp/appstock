package com.example.appstock.ui.products

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.appstock.R
import com.example.appstock.data.Product

/**
 * RecyclerView adapter for displaying a list of [Product]s. Uses a [ListAdapter]
 * with a diff callback to efficiently update the list.
 */
class ProductAdapter(private val onItemClick: (Product) -> Unit) :
    ListAdapter<Product, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = getItem(position)
        holder.bind(product)
        holder.itemView.setOnClickListener { onItemClick(product) }
    }

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameText: TextView = itemView.findViewById(R.id.text_product_name)
        private val priceText: TextView = itemView.findViewById(R.id.text_product_price)
        private val quantityText: TextView = itemView.findViewById(R.id.text_product_quantity)

        fun bind(product: Product) {
            nameText.text = product.name
            priceText.text = String.format("%.2f DH", product.price)
            quantityText.text = "Quantité: ${product.quantity}"
        }
    }

    class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
}