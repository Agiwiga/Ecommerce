package com.example.ecommerce.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ecommerce.R
import com.example.ecommerce.data.SupplierRepository

class EditSupplierActivity : AppCompatActivity() {

    private lateinit var repository: SupplierRepository

    private lateinit var nameEditText: EditText
    private lateinit var addressEditText: EditText
    private lateinit var phoneEditText: EditText

    private var supplierId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_supplier)

        repository = SupplierRepository(this)

        nameEditText = findViewById(R.id.editTextSupplierName)
        addressEditText = findViewById(R.id.editTextSupplierAddress)
        phoneEditText = findViewById(R.id.editTextSupplierPhone)

        val saveButton = findViewById<Button>(R.id.buttonSaveSupplier)
        val cancelButton = findViewById<Button>(R.id.buttonCancelSupplier)

        supplierId = intent.getIntExtra(EXTRA_ID, 0)

        nameEditText.setText(intent.getStringExtra(EXTRA_NAME).orEmpty())
        addressEditText.setText(intent.getStringExtra(EXTRA_ADDRESS).orEmpty())
        phoneEditText.setText(intent.getStringExtra(EXTRA_PHONE).orEmpty())

        saveButton.text = "Update"

        saveButton.setOnClickListener {
            updateSupplier()
        }

        cancelButton.setOnClickListener {
            finish()
        }
    }

    private fun updateSupplier() {

        val success = repository.updateSupplier(
            supplierId,
            nameEditText.text.toString().trim(),
            addressEditText.text.toString().trim(),
            phoneEditText.text.toString().trim()
        )

        if (success) {
            Toast.makeText(this, "Supplier berhasil diupdate", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Gagal mengupdate supplier", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val EXTRA_ID = "id"
        const val EXTRA_NAME = "name"
        const val EXTRA_ADDRESS = "address"
        const val EXTRA_PHONE = "phone"
    }
}