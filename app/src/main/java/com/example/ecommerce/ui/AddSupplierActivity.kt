package com.example.ecommerce.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ecommerce.R
import com.example.ecommerce.data.SupplierRepository

class AddSupplierActivity : AppCompatActivity() {

    private lateinit var repository: SupplierRepository

    private lateinit var nameEditText: EditText
    private lateinit var addressEditText: EditText
    private lateinit var phoneEditText: EditText

    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_supplier)

        repository = SupplierRepository(this)

        nameEditText = findViewById(R.id.editTextSupplierName)
        addressEditText = findViewById(R.id.editTextSupplierAddress)
        phoneEditText = findViewById(R.id.editTextSupplierPhone)

        saveButton = findViewById(R.id.buttonSaveSupplier)
        cancelButton = findViewById(R.id.buttonCancelSupplier)

        saveButton.setOnClickListener {
            saveSupplier()
        }

        cancelButton.setOnClickListener {
            finish()
        }
    }

    private fun saveSupplier() {

        val name = nameEditText.text.toString().trim()
        val address = addressEditText.text.toString().trim()
        val phone = phoneEditText.text.toString().trim()

        if (name.isEmpty()) {
            nameEditText.error = "Nama supplier wajib diisi"
            return
        }

        if (address.isEmpty()) {
            addressEditText.error = "Alamat wajib diisi"
            return
        }

        if (phone.isEmpty()) {
            phoneEditText.error = "Nomor telepon wajib diisi"
            return
        }

        val success = repository.addSupplier(
            name,
            address,
            phone
        )

        if (success) {
            Toast.makeText(this, "Supplier berhasil ditambahkan", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Gagal menambahkan supplier", Toast.LENGTH_SHORT).show()
        }
    }
}