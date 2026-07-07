package com.example.ecommerce.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerce.R
import com.example.ecommerce.adapter.AdminOrderAdapter
import com.example.ecommerce.data.AdminOrderRepository

class OrderManagementActivity : AppCompatActivity() {
    private lateinit var orderRepository: AdminOrderRepository
    private lateinit var ordersRecyclerView: RecyclerView
    private lateinit var emptyTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_management)

        orderRepository = AdminOrderRepository(this)
        ordersRecyclerView = findViewById(R.id.recyclerViewAdminOrders)
        emptyTextView = findViewById(R.id.textViewEmptyAdminOrders)
    }

    override fun onResume() {
        super.onResume()
        showOrders()
    }

    private fun showOrders() {
        val orders = orderRepository.getAllOrders()
        emptyTextView.visibility = if (orders.isEmpty()) View.VISIBLE else View.GONE
        ordersRecyclerView.visibility = if (orders.isEmpty()) View.GONE else View.VISIBLE

        ordersRecyclerView.layoutManager = LinearLayoutManager(this)
        ordersRecyclerView.adapter = AdminOrderAdapter(orders) { order ->
            val intent = Intent(this, AdminOrderDetailActivity::class.java).apply {
                putExtra(AdminOrderDetailActivity.EXTRA_ORDER_ID, order.id)
            }
            startActivity(intent)
        }
    }
}
