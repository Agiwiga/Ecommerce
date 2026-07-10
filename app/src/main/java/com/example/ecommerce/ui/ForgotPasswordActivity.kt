package com.example.ecommerce.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ecommerce.R
import com.example.ecommerce.data.DatabaseHelper

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var databaseHelper: DatabaseHelper

    private lateinit var emailEditText: EditText
    private lateinit var newPasswordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        databaseHelper = DatabaseHelper(this)

        emailEditText = findViewById(R.id.editTextEmail)
        newPasswordEditText = findViewById(R.id.editTextNewPassword)
        confirmPasswordEditText =
            findViewById(R.id.editTextConfirmPassword)

        saveButton = findViewById(R.id.buttonSavePassword)

        saveButton.setOnClickListener {
            resetPassword()
        }
    }

    private fun resetPassword() {

        val email = emailEditText.text.toString().trim()
        val newPassword = newPasswordEditText.text.toString().trim()
        val confirmPassword =
            confirmPasswordEditText.text.toString().trim()

        if (email.isEmpty() ||
            newPassword.isEmpty() ||
            confirmPassword.isEmpty()
        ) {

            Toast.makeText(
                this,
                "Lengkapi semua data",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        if (newPassword != confirmPassword) {

            Toast.makeText(
                this,
                "Konfirmasi password tidak sama",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        if (!databaseHelper.isEmailExists(email)) {

            Toast.makeText(
                this,
                "Email tidak ditemukan",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        val success =
            databaseHelper.updatePassword(email, newPassword)

        if (success) {

            Toast.makeText(
                this,
                "Password berhasil diperbarui",
                Toast.LENGTH_LONG
            ).show()

            finish()

        } else {

            Toast.makeText(
                this,
                "Gagal mengubah password",
                Toast.LENGTH_SHORT
            ).show()

        }
    }
}