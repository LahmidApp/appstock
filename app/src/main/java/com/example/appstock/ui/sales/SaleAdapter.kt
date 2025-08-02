package com.example.appstock.ui.sales

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appstock.R
import com.example.appstock.data.Sale
import com.example.appstock.data.Product

class SaleAdapter(
    private val sales: List<Sale>,
    private val products: Map<Long, Product>,
    private val onClick: (Sale) -> Unit = {}
) : RecyclerView.Adapter<SaleAdapter.SaleViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SaleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sale, parent, false)
        return SaleViewHolder(view)
    }

    override fun onBindViewHolder(holder: SaleViewHolder, position: Int) {
        val sale = sales[position]
        val product = products[sale.productId]
        holder.bind(sale, product)
        holder.itemView.setOnClickListener { onClick(sale) }
    }

    override fun getItemCount(): Int = sales.size

    class SaleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productName: TextView = itemView.findViewById(R.id.sale_product_name)
        private val saleQuantity: TextView = itemView.findViewById(R.id.sale_quantity)
        private val saleTotal: TextView = itemView.findViewById(R.id.sale_total)
        private val saleDate: TextView = itemView.findViewById(R.id.sale_date)

        fun bind(sale: Sale, product: Product?) {
            productName.text = product?.name ?: "Produit inconnu"
            saleQuantity.text = "Quantité: ${sale.quantity}"
            saleTotal.text = "Total: ${sale.totalPrice} Dhs"
            saleDate.text = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(java.util.Date(sale.dateTimestamp))
        }
    }
}
