package com.example.ecommerce.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerce.R
import com.example.ecommerce.adapter.OrderAdapter
import com.example.ecommerce.data.OrderRepository
import com.example.ecommerce.session.SessionManager

class OrderHistoryActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager
    private lateinit var orderRepository: OrderRepository
    private lateinit var ordersRecyclerView: RecyclerView
    private lateinit var emptyTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_history)

        sessionManager = SessionManager(this)
        orderRepository = OrderRepository(this)
        ordersRecyclerView = findViewById(R.id.recyclerViewOrderHistory)
        emptyTextView = findViewById(R.id.textViewEmptyOrderHistory)
    }

    override fun onResume() {
        super.onResume()
        showOrders()
    }

    private fun showOrders() {
        val orders = orderRepository.getOrdersByUser(sessionManager.getUserId())
        emptyTextView.visibility = if (orders.isEmpty()) View.VISIBLE else View.GONE
        ordersRecyclerView.visibility = if (orders.isEmpty()) View.GONE else View.VISIBLE

        ordersRecyclerView.layoutManager = LinearLayoutManager(this)
        ordersRecyclerView.adapter = OrderAdapter(orders) { order ->
            val intent = Intent(this, OrderDetailActivity::class.java).apply {
                putExtra(OrderDetailActivity.EXTRA_ORDER_ID, order.id)
            }
            startActivity(intent)
        }
    }
}
