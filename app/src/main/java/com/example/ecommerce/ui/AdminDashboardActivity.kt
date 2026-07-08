package com.example.ecommerce.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.ecommerce.R
import com.example.ecommerce.data.DatabaseHelper
import com.example.ecommerce.session.SessionManager

class AdminDashboardActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager
    private lateinit var adminNameTextView: TextView
    private lateinit var logoutButton: Button
    private lateinit var manageProductsButton: Button
    private lateinit var manageSuppliersButton: Button
    private lateinit var manageOrdersButton: Button
    private lateinit var databaseHelper: DatabaseHelper

    private lateinit var totalProductsTextView: TextView
    private lateinit var totalOrdersTextView: TextView
    private lateinit var processingTextView: TextView
    private lateinit var shippedTextView: TextView
    private lateinit var finishedTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        sessionManager = SessionManager(this)
        databaseHelper = DatabaseHelper(this)
        adminNameTextView = findViewById(R.id.textViewAdminName)
        logoutButton = findViewById(R.id.buttonAdminLogout)
        manageProductsButton = findViewById(R.id.buttonManageProducts)
        manageSuppliersButton = findViewById(R.id.buttonManageSuppliers)
        manageOrdersButton = findViewById(R.id.buttonManageOrders)
        totalProductsTextView = findViewById(R.id.textViewTotalProducts)
        totalOrdersTextView = findViewById(R.id.textViewTotalOrders)
        processingTextView = findViewById(R.id.textViewProcessing)
        shippedTextView = findViewById(R.id.textViewShipped)
        finishedTextView = findViewById(R.id.textViewFinished)

        adminNameTextView.text = sessionManager.getUserName()
        loadDashboardStatistics()

        logoutButton.setOnClickListener {
            logout()
        }
        manageProductsButton.setOnClickListener {
            startActivity(Intent(this, ProductManagementActivity::class.java))
        }
        manageSuppliersButton.setOnClickListener {
            startActivity(Intent(this, SupplierManagementActivity::class.java))
        }
        manageOrdersButton.setOnClickListener {
            startActivity(Intent(this, OrderManagementActivity::class.java))
        }
    }
    override fun onResume() {
        super.onResume()
        loadDashboardStatistics()
    }

    private fun loadDashboardStatistics() {
        totalProductsTextView.text =
            "📦 Total Produk : ${databaseHelper.getTotalProducts()}"

        totalOrdersTextView.text =
            "📋 Total Pesanan : ${databaseHelper.getTotalOrders()}"

        processingTextView.text =
            "⏳ Diproses : ${databaseHelper.getTotalOrdersByStatus("Diproses")}"

        shippedTextView.text =
            "🚚 Dikirim : ${databaseHelper.getTotalOrdersByStatus("Dikirim")}"

        finishedTextView.text =
            "✅ Selesai : ${databaseHelper.getTotalOrdersByStatus("Selesai")}"
    }

    private fun logout() {
        sessionManager.logout()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
