package com.example.ecommerce.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerce.R
import com.example.ecommerce.model.Order
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderAdapter(
    private val orders: List<Order>,
    private val onOrderClick: (Order) -> Unit
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orders[position])
    }

    override fun getItemCount(): Int = orders.size

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val idTextView: TextView = itemView.findViewById(R.id.textViewOrderId)
        private val dateTextView: TextView = itemView.findViewById(R.id.textViewOrderDate)
        private val statusTextView: TextView = itemView.findViewById(R.id.textViewOrderStatus)
        private val totalTextView: TextView = itemView.findViewById(R.id.textViewOrderTotal)

        fun bind(order: Order) {
            idTextView.text = "Pesanan #${order.id}"
            dateTextView.text = formatDate(order.createdAt)
            statusTextView.text = "Status: ${order.status}"
            totalTextView.text = "Total: ${formatPrice(order.totalPrice)}"
            itemView.setOnClickListener {
                onOrderClick(order)
            }
        }
    }

    private fun formatPrice(price: Double): String {
        return NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID")).format(price)
    }

    private fun formatDate(timestamp: Long): String {
        return SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.forLanguageTag("id-ID"))
            .format(Date(timestamp))
    }
}
