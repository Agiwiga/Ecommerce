package com.example.ecommerce.ui

import android.content.ContentValues
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ecommerce.R
import com.example.ecommerce.data.DatabaseHelper

class AddProductActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var productNameEditText: EditText
    private lateinit var productPriceEditText: EditText
    private lateinit var productDescriptionEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        databaseHelper = DatabaseHelper(this)
        productNameEditText = findViewById(R.id.editTextProductName)
        productPriceEditText = findViewById(R.id.editTextProductPrice)
        productDescriptionEditText = findViewById(R.id.editTextProductDescription)
        saveButton = findViewById(R.id.buttonSaveProduct)
        cancelButton = findViewById(R.id.buttonCancelProduct)

        saveButton.setOnClickListener {
            saveProduct()
        }

        cancelButton.setOnClickListener {
            finish()
        }
    }

    private fun saveProduct() {
        val name = productNameEditText.text.toString().trim()
        val priceText = productPriceEditText.text.toString().trim()
        val description = productDescriptionEditText.text.toString().trim()

        if (name.isEmpty()) {
            productNameEditText.error = "Nama produk wajib diisi"
            return
        }

        if (priceText.isEmpty()) {
            productPriceEditText.error = "Harga wajib diisi"
            return
        }

        if (description.isEmpty()) {
            productDescriptionEditText.error = "Deskripsi wajib diisi"
            return
        }

        val price = priceText.toDoubleOrNull()
        if (price == null || price <= 0.0) {
            productPriceEditText.error = "Harga harus berupa angka positif"
            return
        }

        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_PRODUCT_NAME, name)
            put(DatabaseHelper.COLUMN_PRODUCT_PRICE, price)
            put(DatabaseHelper.COLUMN_PRODUCT_DESCRIPTION, description)
            put(DatabaseHelper.COLUMN_PRODUCT_IMAGE_URL, "")
            put(DatabaseHelper.COLUMN_PRODUCT_STOCK, 0)
        }

        val result = databaseHelper.writableDatabase.insert(
            DatabaseHelper.TABLE_PRODUCTS,
            null,
            values
        )

        if (result != -1L) {
            Toast.makeText(this, "Produk berhasil ditambahkan", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Produk gagal ditambahkan", Toast.LENGTH_SHORT).show()
        }
    }
}
