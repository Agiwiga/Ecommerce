package com.example.ecommerce.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
            Log.d(TAG, "Tombol Login ditekan")
            handleLogin()
        }

        registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun handleLogin() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        Log.d(TAG, "Mulai proses login untuk email: $email")

        if (email.isEmpty()) {
            Log.d(TAG, "Login dibatalkan: email kosong")
            emailEditText.error = "Email tidak boleh kosong"
            return
        }

        if (password.isEmpty()) {
            Log.d(TAG, "Login dibatalkan: password kosong")
            passwordEditText.error = "Password tidak boleh kosong"
            return
        }

        Log.d(TAG, "Jumlah user di database: ${countUsers()}")
        val user = findUserByEmailAndPassword(email, password)
        if (user != null) {
            Log.d(TAG, "Login berhasil untuk userId: ${user.id}, userName: ${user.name}")
            sessionManager.saveLogin(user.id, user.name)
            Log.d(TAG, "SessionManager.saveLogin() sudah dipanggil")
            navigateToHome()
        } else {
            Log.d(TAG, "Login gagal: email atau password tidak cocok")
            Toast.makeText(this, "Email atau Password salah", Toast.LENGTH_SHORT).show()
        }
    }

    private fun findUserByEmailAndPassword(email: String, password: String): LoggedInUser? {
        val database = databaseHelper.readableDatabase
        Log.d(TAG, "Menjalankan query login ke tabel ${DatabaseHelper.TABLE_USERS}")
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
            Log.d(TAG, "Hasil query login ditemukan: ${it.count} baris")
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

    private fun countUsers(): Int {
        val database = databaseHelper.readableDatabase
        val cursor = database.rawQuery(
            "SELECT COUNT(*) FROM ${DatabaseHelper.TABLE_USERS}",
            null
        )

        cursor.use {
            return if (it.moveToFirst()) it.getInt(0) else 0
        }
    }

    private fun navigateToHome() {
        Toast.makeText(this, "Login berhasil", Toast.LENGTH_LONG).show()
        Log.d(TAG, "HomeActivity belum tersedia, tetap berada di LoginActivity")
        // TODO: Arahkan ke HomeActivity setelah HomeActivity dibuat.
    }

    private data class LoggedInUser(
        val id: Int,
        val name: String
    )

    companion object {
        private const val TAG = "LoginActivity"
    }
}
