package com.example.ecommerce.ui

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerce.R
import com.example.ecommerce.adapter.AdminProductAdapter
import com.example.ecommerce.data.DatabaseHelper
import com.example.ecommerce.model.Product

class ProductManagementActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var productsRecyclerView: RecyclerView
    private lateinit var addProductButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_management)

        databaseHelper = DatabaseHelper(this)
        productsRecyclerView = findViewById(R.id.recyclerViewAdminProducts)
        addProductButton = findViewById(R.id.buttonAddProduct)

        showProducts()

        addProductButton.setOnClickListener {
            Toast.makeText(this, "Fitur akan dibuat pada tahap berikutnya", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun showProducts() {
        val adapter = AdminProductAdapter(getProducts()) { product ->
            Toast.makeText(this, product.name, Toast.LENGTH_SHORT).show()
        }

        productsRecyclerView.layoutManager = LinearLayoutManager(this)
        productsRecyclerView.adapter = adapter
    }

    private fun getProducts(): List<Product> {
        val products = mutableListOf<Product>()
        val database = databaseHelper.readableDatabase
        val cursor = database.query(
            DatabaseHelper.TABLE_PRODUCTS,
            arrayOf(
                DatabaseHelper.COLUMN_ID,
                DatabaseHelper.COLUMN_PRODUCT_NAME,
                DatabaseHelper.COLUMN_PRODUCT_DESCRIPTION,
                DatabaseHelper.COLUMN_PRODUCT_PRICE,
                DatabaseHelper.COLUMN_PRODUCT_IMAGE_URL,
                DatabaseHelper.COLUMN_PRODUCT_STOCK
            ),
            null,
            null,
            null,
            null,
            DatabaseHelper.COLUMN_PRODUCT_NAME
        )

        cursor.use {
            while (it.moveToNext()) {
                val idIndex = it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)
                val nameIndex = it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_NAME)
                val descriptionIndex =
                    it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_DESCRIPTION)
                val priceIndex = it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_PRICE)
                val imageUrlIndex =
                    it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_IMAGE_URL)
                val stockIndex = it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_STOCK)

                products.add(
                    Product(
                        id = it.getInt(idIndex),
                        name = it.getString(nameIndex),
                        description = it.getString(descriptionIndex),
                        price = it.getDouble(priceIndex),
                        imageUrl = it.getString(imageUrlIndex) ?: "",
                        stock = it.getInt(stockIndex)
                    )
                )
            }
        }

        return products
    }
}
