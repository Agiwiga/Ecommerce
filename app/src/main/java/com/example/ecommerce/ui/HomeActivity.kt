package com.example.ecommerce.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerce.R
import com.example.ecommerce.adapter.ProductAdapter
import com.example.ecommerce.data.DatabaseHelper
import com.example.ecommerce.model.Product
import com.example.ecommerce.session.SessionManager

class HomeActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var userNameTextView: TextView
    private lateinit var userEmailTextView: TextView
    private lateinit var logoutButton: Button
    private lateinit var productsRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        sessionManager = SessionManager(this)
        databaseHelper = DatabaseHelper(this)
        userNameTextView = findViewById(R.id.textViewUserName)
        userEmailTextView = findViewById(R.id.textViewUserEmail)
        logoutButton = findViewById(R.id.buttonLogout)
        productsRecyclerView = findViewById(R.id.recyclerViewProducts)

        showLoggedInUser()
        showProducts()

        logoutButton.setOnClickListener {
            logout()
        }
    }

    private fun showLoggedInUser() {
        userNameTextView.text = sessionManager.getUserName()

        val userEmail = sessionManager.getUserEmail()
        userEmailTextView.text = if (userEmail.isNotEmpty()) {
            userEmail
        } else {
            "Email belum tersedia"
        }
    }

    private fun showProducts() {
        val products = getProducts()
        val productAdapter = ProductAdapter(products) { product ->
            Toast.makeText(this, product.name, Toast.LENGTH_SHORT).show()
        }

        productsRecyclerView.layoutManager = LinearLayoutManager(this)
        productsRecyclerView.adapter = productAdapter
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
                DatabaseHelper.COLUMN_PRODUCT_STOCK,
                DatabaseHelper.COLUMN_PRODUCT_CATEGORY,
                DatabaseHelper.COLUMN_PRODUCT_SALE_TYPE,
                DatabaseHelper.COLUMN_PRODUCT_PACKAGE_QUANTITY
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
                val categoryIndex =
                    it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_CATEGORY)

                val saleTypeIndex =
                    it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_SALE_TYPE)

                val packageQuantityIndex =
                    it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_PACKAGE_QUANTITY)

                products.add(
                    Product(
                        id = it.getInt(idIndex),
                        name = it.getString(nameIndex),
                        description = it.getString(descriptionIndex),
                        price = it.getDouble(priceIndex),
                        imageUrl = it.getString(imageUrlIndex) ?: "",
                        stock = it.getDouble(stockIndex),
                        category = it.getString(categoryIndex),
                        saleType = it.getString(saleTypeIndex),
                        packageQuantity = it.getDouble(packageQuantityIndex)
                    )
                )
            }
        }

        return products
    }

    private fun logout() {
        sessionManager.logout()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
