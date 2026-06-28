package com.example.ecommerce.ui

import android.content.ContentValues
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ecommerce.R
import com.example.ecommerce.data.DatabaseHelper

class EditProductActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var productNameEditText: EditText
    private lateinit var productPriceEditText: EditText
    private lateinit var productDescriptionEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private var productId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_product)

        databaseHelper = DatabaseHelper(this)
        productNameEditText = findViewById(R.id.editTextEditProductName)
        productPriceEditText = findViewById(R.id.editTextEditProductPrice)
        productDescriptionEditText = findViewById(R.id.editTextEditProductDescription)
        saveButton = findViewById(R.id.buttonUpdateProduct)
        cancelButton = findViewById(R.id.buttonCancelEditProduct)

        loadProductData()

        saveButton.setOnClickListener {
            updateProduct()
        }

        cancelButton.setOnClickListener {
            finish()
        }
    }

    private fun loadProductData() {
        productId = intent.getIntExtra(EXTRA_PRODUCT_ID, 0)
        productNameEditText.setText(intent.getStringExtra(EXTRA_PRODUCT_NAME).orEmpty())
        productPriceEditText.setText(intent.getDoubleExtra(EXTRA_PRODUCT_PRICE, 0.0).toString())
        productDescriptionEditText.setText(
            intent.getStringExtra(EXTRA_PRODUCT_DESCRIPTION).orEmpty()
        )
    }

    private fun updateProduct() {
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
        }

        val updatedRows = databaseHelper.writableDatabase.update(
            DatabaseHelper.TABLE_PRODUCTS,
            values,
            "${DatabaseHelper.COLUMN_ID} = ?",
            arrayOf(productId.toString())
        )

        if (updatedRows > 0) {
            Toast.makeText(this, "Produk berhasil diperbarui", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Produk gagal diperbarui", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val EXTRA_PRODUCT_ID = "extra_product_id"
        const val EXTRA_PRODUCT_NAME = "extra_product_name"
        const val EXTRA_PRODUCT_PRICE = "extra_product_price"
        const val EXTRA_PRODUCT_DESCRIPTION = "extra_product_description"
    }
}
