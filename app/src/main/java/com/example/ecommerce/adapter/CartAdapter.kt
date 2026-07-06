package com.example.ecommerce.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerce.R
import com.example.ecommerce.model.Cart
import java.text.NumberFormat
import java.util.Locale

class CartAdapter(
    private val items: List<Cart>,
    private val onEditClick: (Cart) -> Unit,
    private val onDeleteClick: (Cart) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.textViewCartProductName)
        private val categoryTextView: TextView = itemView.findViewById(R.id.textViewCartCategory)
        private val purchaseTypeTextView: TextView =
            itemView.findViewById(R.id.textViewCartPurchaseType)
        private val inputQuantityTextView: TextView =
            itemView.findViewById(R.id.textViewCartInputQuantity)
        private val actualQuantityTextView: TextView =
            itemView.findViewById(R.id.textViewCartActualQuantity)
        private val totalPriceTextView: TextView = itemView.findViewById(R.id.textViewCartTotalPrice)
        private val editButton: Button = itemView.findViewById(R.id.buttonEditCartItem)
        private val deleteButton: Button = itemView.findViewById(R.id.buttonDeleteCartItem)

        fun bind(item: Cart) {
            val product = item.product
            nameTextView.text = product?.name.orEmpty()
            categoryTextView.text = "Kategori: ${product?.category.orEmpty()}"
            purchaseTypeTextView.text = "Cara pembelian: ${formatPurchaseType(item.purchaseType)}"
            inputQuantityTextView.text = "Jumlah input: ${formatQuantity(item.inputQuantity)}"
            actualQuantityTextView.text =
                "Jumlah aktual: ${formatQuantity(item.actualQuantity)} ${getActualUnit(product?.saleType)}"
            totalPriceTextView.text = "Total: ${formatPrice(item.totalPrice)}"

            editButton.setOnClickListener {
                onEditClick(item)
            }
            deleteButton.setOnClickListener {
                onDeleteClick(item)
            }
        }
    }

    private fun formatPurchaseType(purchaseType: String): String {
        return when (purchaseType) {
            "kg" -> "Kg"
            "karung" -> "Karung"
            "pcs" -> "Pcs"
            "pack" -> "Pack"
            "nominal" -> "Nominal"
            else -> purchaseType
        }
    }

    private fun getActualUnit(saleType: String?): String {
        return if (saleType == "Berat") "Kg" else "pcs"
    }

    private fun formatPrice(price: Double): String {
        return NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID")).format(price)
    }

    private fun formatQuantity(quantity: Double): String {
        return if (quantity % 1.0 == 0.0) {
            quantity.toInt().toString()
        } else {
            String.format(Locale.US, "%.2f", quantity).trimEnd('0').trimEnd('.')
        }
    }
}
