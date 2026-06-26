package com.example.ecommerce.ui

import android.content.ContentValues
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ecommerce.R
import com.example.ecommerce.data.DatabaseHelper

class RegisterActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var registerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        databaseHelper = DatabaseHelper(this)

        nameEditText = findViewById(R.id.editTextName)
        emailEditText = findViewById(R.id.editTextRegisterEmail)
        passwordEditText = findViewById(R.id.editTextRegisterPassword)
        confirmPasswordEditText = findViewById(R.id.editTextConfirmPassword)
        registerButton = findViewById(R.id.buttonRegister)

        registerButton.setOnClickListener {
            handleRegister()
        }
    }

    private fun handleRegister() {
        val name = nameEditText.text.toString().trim()
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        val confirmPassword = confirmPasswordEditText.text.toString().trim()

        if (name.isEmpty()) {
            nameEditText.error = "Nama wajib diisi"
            return
        }

        if (email.isEmpty()) {
            emailEditText.error = "Email wajib diisi"
            return
        }

        if (password.isEmpty()) {
            passwordEditText.error = "Password wajib diisi"
            return
        }

        if (confirmPassword.isEmpty()) {
            confirmPasswordEditText.error = "Konfirmasi password wajib diisi"
            return
        }

        if (password != confirmPassword) {
            confirmPasswordEditText.error = "Password dan konfirmasi password harus sama"
            return
        }

        if (isEmailRegistered(email)) {
            emailEditText.error = "Email sudah terdaftar"
            return
        }

        if (saveUser(name, email, password)) {
            Toast.makeText(this, "Registrasi berhasil", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Registrasi gagal", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isEmailRegistered(email: String): Boolean {
        val database = databaseHelper.readableDatabase
        val cursor = database.query(
            DatabaseHelper.TABLE_USERS,
            arrayOf(DatabaseHelper.COLUMN_ID),
            "${DatabaseHelper.COLUMN_USER_EMAIL} = ?",
            arrayOf(email),
            null,
            null,
            null,
            "1"
        )

        cursor.use {
            return it.moveToFirst()
        }
    }

    private fun saveUser(name: String, email: String, password: String): Boolean {
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_USER_NAME, name)
            put(DatabaseHelper.COLUMN_USER_EMAIL, email)
            put(DatabaseHelper.COLUMN_USER_PASSWORD, password)
            put(DatabaseHelper.COLUMN_USER_ROLE, DatabaseHelper.ROLE_CUSTOMER)
        }

        val result = databaseHelper.writableDatabase.insert(
            DatabaseHelper.TABLE_USERS,
            null,
            values
        )

        return result != -1L
    }
}
