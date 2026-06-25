package com.example.ecommerce.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ecommerce.R
import com.example.ecommerce.data.DatabaseHelper
import com.example.ecommerce.session.SessionManager

class LoginActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var sessionManager: SessionManager
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        databaseHelper = DatabaseHelper(this)
        sessionManager = SessionManager(this)

        emailEditText = findViewById(R.id.editTextEmail)
        passwordEditText = findViewById(R.id.editTextPassword)
        loginButton = findViewById(R.id.buttonLogin)
        registerButton = findViewById(R.id.buttonOpenRegister)

        loginButton.setOnClickListener {
            handleLogin()
        }

        registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun handleLogin() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        if (email.isEmpty()) {
            emailEditText.error = "Email tidak boleh kosong"
            return
        }

        if (password.isEmpty()) {
            passwordEditText.error = "Password tidak boleh kosong"
            return
        }

        val user = findUserByEmailAndPassword(email, password)
        if (user != null) {
            sessionManager.saveLogin(user.id, user.name)
            navigateToHome()
        } else {
            Toast.makeText(this, "Email atau Password salah", Toast.LENGTH_SHORT).show()
        }
    }

    private fun findUserByEmailAndPassword(email: String, password: String): LoggedInUser? {
        val database = databaseHelper.readableDatabase
        val cursor = database.query(
            DatabaseHelper.TABLE_USERS,
            arrayOf(DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_USER_NAME),
            "${DatabaseHelper.COLUMN_USER_EMAIL} = ? AND ${DatabaseHelper.COLUMN_USER_PASSWORD} = ?",
            arrayOf(email, password),
            null,
            null,
            null,
            "1"
        )

        cursor.use {
            if (it.moveToFirst()) {
                val idIndex = it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)
                val nameIndex = it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_NAME)
                return LoggedInUser(
                    id = it.getInt(idIndex),
                    name = it.getString(nameIndex)
                )
            }
        }

        return null
    }

    private fun navigateToHome() {
        Toast.makeText(
            this,
            "Login berhasil, tetapi HomeActivity belum tersedia",
            Toast.LENGTH_SHORT
        ).show()
        // TODO: Arahkan ke HomeActivity setelah HomeActivity dibuat.
    }

    private data class LoggedInUser(
        val id: Int,
        val name: String
    )
}
