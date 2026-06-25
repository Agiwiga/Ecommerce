package com.example.ecommerce.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.ecommerce.R
import com.example.ecommerce.session.SessionManager

class HomeActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager
    private lateinit var userNameTextView: TextView
    private lateinit var userEmailTextView: TextView
    private lateinit var logoutButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        sessionManager = SessionManager(this)
        userNameTextView = findViewById(R.id.textViewUserName)
        userEmailTextView = findViewById(R.id.textViewUserEmail)
        logoutButton = findViewById(R.id.buttonLogout)

        showLoggedInUser()

        logoutButton.setOnClickListener {
            logout()
        }
    }

    private fun showLoggedInUser() {
        userNameTextView.text = sessionManager.getUserName()

        val userEmail = sessionManager.getUserEmail()
        userEmailTextView.text = if (userEmail.isNotEmpty()) {
            userEmail
        } else {
            "Email belum tersedia"
        }
    }

    private fun logout() {
        sessionManager.logout()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
