package com.example.ecommerce.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerce.R
import com.example.ecommerce.adapter.OrderItemAdapter
import com.example.ecommerce.data.OrderRepository
import com.example.ecommerce.model.Order
import com.example.ecommerce.session.SessionManager
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderDetailActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager
    private lateinit var orderRepository: OrderRepository
    private lateinit var orderInfoTextView: TextView
    private lateinit var itemsRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)

        sessionManager = SessionManager(this)
        orderRepository = OrderRepository(this)
        orderInfoTextView = findViewById(R.id.textViewOrderDetailInfo)
        itemsRecyclerView = findViewById(R.id.recyclerViewOrderDetailItems)

        showOrderDetail()
    }

    private fun showOrderDetail() {
        val orderId = intent.getIntExtra(EXTRA_ORDER_ID, 0)
        val order = orderRepository.getOrderById(orderId, sessionManager.getUserId())
        if (order == null) {
            orderInfoTextView.text = "Pesanan tidak ditemukan"
            return
        }

        showOrderHeader(order)
        val items = orderRepository.getOrderItems(order.id)
        itemsRecyclerView.layoutManager = LinearLayoutManager(this)
        itemsRecyclerView.adapter = OrderItemAdapter(items)
    }

    private fun showOrderHeader(order: Order) {
        orderInfoTextView.text = """
            Pesanan #${order.id}
            Tanggal: ${formatDate(order.createdAt)}
            Status: ${order.status}
            Total pembayaran: ${formatPrice(order.totalPrice)}
        """.trimIndent()
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
    }
}
