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

class EditProductActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var productNameEditText: EditText
    private lateinit var productPriceEditText: EditText
    private lateinit var productDescriptionEditText: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var saleTypeSpinner: Spinner
    private lateinit var packageQuantityEditText: EditText
    private lateinit var stockEditText: EditText
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
        categorySpinner = findViewById(R.id.spinnerEditCategory)
        saleTypeSpinner = findViewById(R.id.spinnerEditSaleType)
        packageQuantityEditText = findViewById(R.id.editTextEditPackageQuantity)
        stockEditText = findViewById(R.id.editTextEditProductStock)



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
        packageQuantityEditText.setText(
            intent.getDoubleExtra(EXTRA_PRODUCT_PACKAGE_QUANTITY, 0.0).toString()
        )

        stockEditText.setText(
            intent.getDoubleExtra(EXTRA_PRODUCT_STOCK, 0.0).toString()
        )
        val category = intent.getStringExtra(EXTRA_PRODUCT_CATEGORY).orEmpty()
        val saleType = intent.getStringExtra(EXTRA_PRODUCT_SALE_TYPE).orEmpty()
        val categoryPosition = (categorySpinner.adapter as ArrayAdapter<String>)
            .getPosition(category)
        categorySpinner.setSelection(categoryPosition)
        val saleTypePosition = (saleTypeSpinner.adapter as ArrayAdapter<String>)
            .getPosition(saleType)

        saleTypeSpinner.setSelection(saleTypePosition)
    }

    private fun updateProduct() {
        val name = productNameEditText.text.toString().trim()
        val priceText = productPriceEditText.text.toString().trim()
        val description = productDescriptionEditText.text.toString().trim()

        val category = categorySpinner.selectedItem.toString()
        val saleType = saleTypeSpinner.selectedItem.toString()

        val packageQuantityText = packageQuantityEditText.text.toString().trim()
        val stockText = stockEditText.text.toString().trim()

        if (packageQuantityText.isEmpty()) {
            packageQuantityEditText.error = "Isi kemasan wajib diisi"
            return
        }

        if (stockText.isEmpty()) {
            stockEditText.error = "Stok wajib diisi"
            return
        }
        val packageQuantity = packageQuantityText.toDoubleOrNull()
        if (packageQuantity == null || packageQuantity <= 0) {
            packageQuantityEditText.error = "Isi kemasan tidak valid"
            return
        }

        val stock = stockText.toDoubleOrNull()
        if (stock == null || stock < 0) {
            stockEditText.error = "Stok tidak valid"
            return
        }

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
            put(DatabaseHelper.COLUMN_PRODUCT_CATEGORY, category)
            put(DatabaseHelper.COLUMN_PRODUCT_SALE_TYPE, saleType)
            put(DatabaseHelper.COLUMN_PRODUCT_PRICE, price)
            put(DatabaseHelper.COLUMN_PRODUCT_PACKAGE_QUANTITY, packageQuantity)
            put(DatabaseHelper.COLUMN_PRODUCT_STOCK, stock)
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
        const val EXTRA_PRODUCT_CATEGORY = "extra_product_category"
        const val EXTRA_PRODUCT_SALE_TYPE = "extra_product_sale_type"
        const val EXTRA_PRODUCT_PACKAGE_QUANTITY = "extra_product_package_quantity"
        const val EXTRA_PRODUCT_STOCK = "extra_product_stock"
    }
    }

