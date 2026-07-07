package com.example.ecommerce.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerce.R
import com.example.ecommerce.adapter.OrderItemAdapter
import com.example.ecommerce.data.AdminOrderRepository
import com.example.ecommerce.model.AdminOrder
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AdminOrderDetailActivity : AppCompatActivity() {
    private lateinit var orderRepository: AdminOrderRepository
    private lateinit var orderInfoTextView: TextView
    private lateinit var statusSpinner: Spinner
    private lateinit var saveStatusButton: Button
    private lateinit var itemsRecyclerView: RecyclerView
    private var orderId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_order_detail)

        orderRepository = AdminOrderRepository(this)
        orderInfoTextView = findViewById(R.id.textViewAdminOrderDetailInfo)
        statusSpinner = findViewById(R.id.spinnerOrderStatus)
        saveStatusButton = findViewById(R.id.buttonSaveOrderStatus)
        itemsRecyclerView = findViewById(R.id.recyclerViewAdminOrderItems)
        orderId = intent.getIntExtra(EXTRA_ORDER_ID, 0)

        setupStatusSpinner()
        showOrderDetail()

        saveStatusButton.setOnClickListener {
            saveStatus()
        }
    }

    private fun setupStatusSpinner() {
        statusSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            STATUSES
        )
    }

    private fun showOrderDetail() {
        val order = orderRepository.getOrderById(orderId)
        if (order == null) {
            orderInfoTextView.text = "Pesanan tidak ditemukan"
            saveStatusButton.isEnabled = false
            return
        }

        showOrderHeader(order)
        statusSpinner.setSelection(STATUSES.indexOf(order.status).coerceAtLeast(0))

        val items = orderRepository.getOrderItems(order.id)
        itemsRecyclerView.layoutManager = LinearLayoutManager(this)
        itemsRecyclerView.adapter = OrderItemAdapter(items)
    }

    private fun showOrderHeader(order: AdminOrder) {
        orderInfoTextView.text = """
            Customer: ${order.customerName}
            Email: ${order.customerEmail}
            Order #${order.id}
            Tanggal: ${formatDate(order.createdAt)}
            Status: ${order.status}
            Total pembayaran: ${formatPrice(order.totalPrice)}
        """.trimIndent()
    }

    private fun saveStatus() {
        val selectedStatus = statusSpinner.selectedItem.toString()
        val updated = orderRepository.updateOrderStatus(orderId, selectedStatus)
        if (updated) {
            Toast.makeText(this, "Status pesanan berhasil diperbarui", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Status pesanan gagal diperbarui", Toast.LENGTH_SHORT).show()
        }
    }

    private fun formatPrice(price: Double): String {
        return NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID")).format(price)
    }

    private fun formatDate(timestamp: Long): String {
        return SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.forLanguageTag("id-ID"))
            .format(Date(timestamp))
    }

    companion object {
        const val EXTRA_ORDER_ID = "extra_order_id"
        private val STATUSES = listOf("Diproses", "Dikemas", "Dikirim", "Selesai")
    }
}
