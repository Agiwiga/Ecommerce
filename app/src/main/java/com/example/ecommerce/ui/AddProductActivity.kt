package com.example.ecommerce.ui

import android.content.ContentValues
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ecommerce.R
import com.example.ecommerce.data.DatabaseHelper
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.AdapterView
import android.view.View

class AddProductActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var productNameEditText: EditText
    private lateinit var productPriceEditText: EditText
    private lateinit var productDescriptionEditText: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var saleTypeSpinner: Spinner
    private lateinit var packageQuantityEditText: EditText
    private lateinit var weightEditText: EditText
    private lateinit var stockEditText: EditText

    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        databaseHelper = DatabaseHelper(this)
        productNameEditText = findViewById(R.id.editTextProductName)
        productPriceEditText = findViewById(R.id.editTextProductPrice)
        productDescriptionEditText = findViewById(R.id.editTextProductDescription)
        categorySpinner = findViewById(R.id.spinnerCategory)
        saleTypeSpinner = findViewById(R.id.spinnerSaleType)
        packageQuantityEditText = findViewById(R.id.editTextPackageQuantity)
        stockEditText = findViewById(R.id.editTextProductStock)
        saveButton = findViewById(R.id.buttonSaveProduct)
        cancelButton = findViewById(R.id.buttonCancelProduct)
        weightEditText = findViewById(R.id.editTextProductWeight)

        val categories = listOf("Pakan", "Vitamin", "Obat", "Peralatan")

        categorySpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            categories
        )

        val saleTypes = listOf("Berat", "Satuan")

        saleTypeSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            saleTypes
        )

        saleTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: android.view.View?,
                position: Int,
                id: Long
            ) {
                updateInputHints(saleTypeSpinner.selectedItem.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        saveButton.setOnClickListener {
            saveProduct()
        }

        cancelButton.setOnClickListener {
            finish()
        }
    }

    private fun updateInputHints(saleType: String) {
        if (saleType == "Berat") {

            productPriceEditText.hint = "Harga per Kg"
            packageQuantityEditText.hint = "Isi Karung (Kg)"
            stockEditText.hint = "Stok (Karung)"
            weightEditText.visibility = View.GONE
        } else {

            productPriceEditText.hint = "Harga per Pcs"
            packageQuantityEditText.hint = "Isi Pack (Pcs)"
            stockEditText.hint = "Stok (Pack)"
            weightEditText.visibility = View.VISIBLE
        }
    }

    private fun saveProduct() {
        val name = productNameEditText.text.toString().trim()
        val priceText = productPriceEditText.text.toString().trim()
        val description = productDescriptionEditText.text.toString().trim()
        val category = categorySpinner.selectedItem.toString()
        val saleType = saleTypeSpinner.selectedItem.toString()
        val packageQuantityText = packageQuantityEditText.text.toString().trim()
        val stockText = stockEditText.text.toString().trim()
        val weightText = weightEditText.text.toString().trim()

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
        if (packageQuantityText.isEmpty()) {
            packageQuantityEditText.error = "Isi kemasan wajib diisi"
            return
        }
        if (stockText.isEmpty()) {
            stockEditText.error = "Stok wajib diisi"
            return
        }
        if (saleType == "Satuan") {

            if (weightText.isEmpty()) {
                weightEditText.error = "Berat wajib diisi"
                return
            }

        }

        val packageQuantity = packageQuantityText.toDoubleOrNull()
        if (packageQuantity == null || packageQuantity <= 0) {
            packageQuantityEditText.error = "Isi kemasan tidak valid"
            return
        }
        val stock = stockText.toDoubleOrNull()
        val weight =
            if (saleType == "Satuan") {
                weightText.toDoubleOrNull() ?: 0.0
            } else {
                1.0
            }
        if (saleType == "Satuan" && weight <= 0) {
            weightEditText.error = "Berat tidak valid"
            return
        }
        if (stock == null || stock < 0) {
            stockEditText.error = "Stok tidak valid"
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
            put(DatabaseHelper.COLUMN_PRODUCT_STOCK, stock)
            put(DatabaseHelper.COLUMN_PRODUCT_CATEGORY, category)
            put(DatabaseHelper.COLUMN_PRODUCT_SALE_TYPE, saleType)
            put(DatabaseHelper.COLUMN_PRODUCT_PACKAGE_QUANTITY, packageQuantity)
            put(DatabaseHelper.COLUMN_PRODUCT_WEIGHT, weight)
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
