package com.example.ecommerce.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerce.R
import com.example.ecommerce.model.Product
import java.text.NumberFormat
import java.util.Locale

class AdminProductAdapter(
    private val products: List<Product>,
    private val onProductClick: (Product) -> Unit,
    private val onDeleteClick: (Product) -> Unit
) : RecyclerView.Adapter<AdminProductAdapter.AdminProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_product, parent, false)
        return AdminProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdminProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int {
        return products.size
    }

    inner class AdminProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.textViewAdminProductName)
        private val priceTextView: TextView = itemView.findViewById(R.id.textViewAdminProductPrice)
        private val descriptionTextView: TextView =
            itemView.findViewById(R.id.textViewAdminProductDescription)
        private val deleteButton: Button = itemView.findViewById(R.id.buttonDeleteProduct)

        fun bind(product: Product) {
            nameTextView.text = product.name
            priceTextView.text = formatPrice(product.price)
            descriptionTextView.text = product.description
            itemView.setOnClickListener {
                onProductClick(product)
            }
            deleteButton.setOnClickListener {
                onDeleteClick(product)
            }
        }
    }

    private fun formatPrice(price: Double): String {
        return NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID")).format(price)
    }
}
