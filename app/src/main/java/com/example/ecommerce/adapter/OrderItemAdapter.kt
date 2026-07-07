package com.example.ecommerce.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerce.R
import com.example.ecommerce.model.OrderItem
import java.text.NumberFormat
import java.util.Locale

class OrderItemAdapter(
    private val items: List<OrderItem>
) : RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_detail, parent, false)
        return OrderItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class OrderItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.textViewOrderItemName)
        private val purchaseTypeTextView: TextView =
            itemView.findViewById(R.id.textViewOrderItemPurchaseType)
        private val inputQuantityTextView: TextView =
            itemView.findViewById(R.id.textViewOrderItemInputQuantity)
        private val actualQuantityTextView: TextView =
            itemView.findViewById(R.id.textViewOrderItemActualQuantity)
        private val itemPriceTextView: TextView =
            itemView.findViewById(R.id.textViewOrderItemPrice)
        private val totalPriceTextView: TextView =
            itemView.findViewById(R.id.textViewOrderItemTotal)

        fun bind(item: OrderItem) {
            nameTextView.text = item.productName
            purchaseTypeTextView.text = "Cara pembelian: ${formatPurchaseType(item.purchaseType)}"
            inputQuantityTextView.text = "Jumlah input: ${formatQuantity(item.inputQuantity)}"
            actualQuantityTextView.text = "Jumlah aktual: ${formatQuantity(item.actualQuantity)}"
            itemPriceTextView.text = "Harga item: ${formatPrice(item.itemPrice)}"
            totalPriceTextView.text = "Total item: ${formatPrice(item.totalPrice)}"
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
