package com.example.ecommerce.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.ecommerce.R
import com.example.ecommerce.session.SessionManager

class AdminDashboardActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager
    private lateinit var adminNameTextView: TextView
    private lateinit var logoutButton: Button
    private lateinit var manageProductsButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        sessionManager = SessionManager(this)
        adminNameTextView = findViewById(R.id.textViewAdminName)
        logoutButton = findViewById(R.id.buttonAdminLogout)
        manageProductsButton = findViewById(R.id.buttonManageProducts)

        adminNameTextView.text = sessionManager.getUserName()

        logoutButton.setOnClickListener {
            logout()
        }

        manageProductsButton.setOnClickListener {
            startActivity(Intent(this, ProductManagementActivity::class.java))
        }
    }

    private fun logout() {
        sessionManager.logout()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
