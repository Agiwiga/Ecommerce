package com.example.ecommerce.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ecommerce.data.DatabaseHelper
import com.example.ecommerce.session.SessionManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sessionManager = SessionManager(this)
        val destination = if (!sessionManager.isLoggedIn()) {
            LoginActivity::class.java
        } else if (sessionManager.getUserRole() == DatabaseHelper.ROLE_ADMIN) {
            AdminDashboardActivity::class.java
        } else {
            HomeActivity::class.java
        }

        startActivity(Intent(this, destination))
        finish()
    }
}
