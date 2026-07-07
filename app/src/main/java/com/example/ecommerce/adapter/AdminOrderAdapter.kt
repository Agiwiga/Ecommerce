package com.example.ecommerce.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerce.R
import com.example.ecommerce.model.AdminOrder
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AdminOrderAdapter(
    private val orders: List<AdminOrder>,
    private val onOrderClick: (AdminOrder) -> Unit
) : RecyclerView.Adapter<AdminOrderAdapter.AdminOrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminOrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_order, parent, false)
        return AdminOrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdminOrderViewHolder, position: Int) {
        holder.bind(orders[position])
    }

    override fun getItemCount(): Int = orders.size

    inner class AdminOrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val idTextView: TextView = itemView.findViewById(R.id.textViewAdminOrderId)
        private val customerTextView: TextView =
            itemView.findViewById(R.id.textViewAdminOrderCustomer)
        private val dateTextView: TextView = itemView.findViewById(R.id.textViewAdminOrderDate)
        private val totalTextView: TextView = itemView.findViewById(R.id.textViewAdminOrderTotal)
        private val statusTextView: TextView = itemView.findViewById(R.id.textViewAdminOrderStatus)

        fun bind(order: AdminOrder) {
            idTextView.text = "Order #${order.id}"
            customerTextView.text = "Customer: ${order.customerName}"
            dateTextView.text = formatDate(order.createdAt)
            totalTextView.text = "Total: ${formatPrice(order.totalPrice)}"
            statusTextView.text = "Status: ${order.status}"
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
