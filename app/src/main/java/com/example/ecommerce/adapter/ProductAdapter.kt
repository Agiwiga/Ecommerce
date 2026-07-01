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

class ProductAdapter(
    private val products: List<Product>,
    private val onDetailClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int {
        return products.size
    }

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.textViewProductName)
        private val priceTextView: TextView = itemView.findViewById(R.id.textViewProductPrice)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.textViewProductDescription)
        private val categoryTextView: TextView = itemView.findViewById(R.id.textViewProductCategory)
        private val unitTextView: TextView = itemView.findViewById(R.id.textViewProductUnit)
        private val stockTextView: TextView = itemView.findViewById(R.id.textViewProductStock)
        private val detailButton: Button = itemView.findViewById(R.id.buttonViewDetail)


        fun bind(product: Product) {
            nameTextView.text = product.name
            priceTextView.text =
                if (product.saleType == "Berat")
                    "${formatPrice(product.price)} / Kg"
                else
                    "${formatPrice(product.price)} / Pcs"

            categoryTextView.text = "Kategori : ${product.category}"
            if (product.saleType == "Berat") {
                unitTextView.text = "Isi Karung : ${product.packageQuantity} Kg"
                stockTextView.text = "Stok : ${product.stock.toInt()} Karung"
            } else {
                unitTextView.text = "Isi Pack : ${product.packageQuantity.toInt()} Pcs"
                stockTextView.text = "Stok : ${product.stock.toInt()} Pack"
            }

            descriptionTextView.text = product.description
            detailButton.setOnClickListener {
                onDetailClick(product)
            }

        }
    }

    private fun formatPrice(price: Double): String {
        return NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID")).format(price)
    }
}
