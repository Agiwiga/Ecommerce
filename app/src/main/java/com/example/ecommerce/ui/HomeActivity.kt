package com.example.ecommerce.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.SearchView
import android.widget.Spinner
import android.widget.TextView
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
    private lateinit var cartButton: Button
    private lateinit var orderHistoryButton: Button

    private lateinit var productsRecyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var categorySpinner: Spinner

    private lateinit var sortSpinner: Spinner

    private var selectedSort = "Nama A-Z"
    // Cache list produk dari DB untuk menghindari query berulang-ulang di UI Thread
    private var allProductsList = listOf<Product>()
    private var selectedCategory = "Semua"
    private var currentKeyword = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        sessionManager = SessionManager(this)
        databaseHelper = DatabaseHelper(this)

        userNameTextView = findViewById(R.id.textViewUserName)
        userEmailTextView = findViewById(R.id.textViewUserEmail)

        logoutButton = findViewById(R.id.buttonLogout)
        cartButton = findViewById(R.id.buttonOpenCart)
        orderHistoryButton = findViewById(R.id.buttonOpenOrderHistory)

        productsRecyclerView = findViewById(R.id.recyclerViewProducts)
        searchView = findViewById(R.id.searchViewProduct)
        categorySpinner = findViewById(R.id.spinnerCategory)
        sortSpinner = findViewById(R.id.spinnerSort)

        // Cukup set LayoutManager SATU KALI di sini
        productsRecyclerView.layoutManager = LinearLayoutManager(this)

        showLoggedInUser()
        setupCategorySpinner()
        setupSortSpinner()

        sortSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedSort =
                        parent?.getItemAtPosition(position).toString()
                    filterProducts()
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }



        logoutButton.setOnClickListener { logout() }
        cartButton.setOnClickListener { startActivity(Intent(this, CartActivity::class.java)) }
        orderHistoryButton.setOnClickListener { startActivity(Intent(this, OrderHistoryActivity::class.java)) }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                currentKeyword = newText.orEmpty()
                filterProducts()
                return true
            }
        })
    }

    override fun onResume() {
        super.onResume()
        // Ambil data terbaru dari DB saat activity aktif kembali
        allProductsList = getProducts()
        filterProducts()
    }

    private fun showLoggedInUser() {
        userNameTextView.text = sessionManager.getUserName()
        val email = sessionManager.getUserEmail()
        userEmailTextView.text = email.ifEmpty { "Email belum tersedia" }
    }

    private fun setupCategorySpinner() {
        val categories = databaseHelper.getAllCategories()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter

        // PERBAIKAN: Listener dipindahkan ke dalam method penyiapan spinner
        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedCategory = parent?.getItemAtPosition(position).toString()
                filterProducts()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun filterProducts() {
        // Menggunakan cache produk
        var filteredProducts = allProductsList.toList()
        // ==========================
        // Filter Kategori
        // ==========================
        if (selectedCategory != "Semua") {
            filteredProducts = filteredProducts.filter {
                it.category.equals(
                    selectedCategory,
                    ignoreCase = true
                )
            }
        }
        // ==========================
        // Search Nama Produk
        // ==========================
        if (currentKeyword.isNotBlank()) {
            filteredProducts = filteredProducts.filter {
                it.name.contains(
                    currentKeyword,
                    ignoreCase = true
                )
            }
        }
        // ==========================
        // Sorting
        // ==========================
        filteredProducts =
            when (selectedSort) {
                "Harga Termurah" ->
                    filteredProducts.sortedBy {
                        it.price
                    }
                "Harga Termahal" ->
                    filteredProducts.sortedByDescending {
                        it.price
                    }
                "Stok Terbanyak" ->

                    filteredProducts.sortedByDescending {
                        it.stock
                    }
                else ->

                    filteredProducts.sortedBy {
                        it.name
                    }
            }
        // ==========================
        // Tampilkan
        // ==========================
        showProducts(filteredProducts)
    }
    private fun setupSortSpinner() {
        val sortOptions = listOf(
            "Nama A-Z",
            "Harga Termurah",
            "Harga Termahal",
            "Stok Terbanyak"
        )
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            sortOptions
        )
        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        sortSpinner.adapter = adapter
    }

    private fun showProducts(products: List<Product>) {
        // Jika menggunakan adapter biasa, ini tidak masalah.
        // Namun disarankan ke depannya memakai ListAdapter dengan DiffUtil agar lebih smooth.
        productsRecyclerView.adapter = ProductAdapter(products) { product ->
            openProductDetail(product)
        }
    }

    private fun openProductDetail(product: Product) {
        val intent = Intent(this, ProductDetailActivity::class.java).apply {
            putExtra(ProductDetailActivity.EXTRA_PRODUCT_ID, product.id)
            putExtra(ProductDetailActivity.EXTRA_PRODUCT_NAME, product.name)
            putExtra(ProductDetailActivity.EXTRA_PRODUCT_DESCRIPTION, product.description)
            putExtra(ProductDetailActivity.EXTRA_PRODUCT_CATEGORY, product.category)
            putExtra(ProductDetailActivity.EXTRA_PRODUCT_SALE_TYPE, product.saleType)
            putExtra(ProductDetailActivity.EXTRA_PRODUCT_PRICE, product.price)
            putExtra(ProductDetailActivity.EXTRA_PRODUCT_PACKAGE_QUANTITY, product.packageQuantity)
            putExtra(ProductDetailActivity.EXTRA_PRODUCT_STOCK, product.stock)
            putExtra(ProductDetailActivity.EXTRA_PRODUCT_WEIGHT, product.weight)
        }
        startActivity(intent)
    }

    private fun getProducts(): List<Product> {
        val products = mutableListOf<Product>()
        val database = databaseHelper.readableDatabase


        val cursor = database.query(
            DatabaseHelper.TABLE_PRODUCTS,
            arrayOf(
                DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_PRODUCT_NAME,
                DatabaseHelper.COLUMN_PRODUCT_DESCRIPTION, DatabaseHelper.COLUMN_PRODUCT_PRICE,
                DatabaseHelper.COLUMN_PRODUCT_IMAGE_URL, DatabaseHelper.COLUMN_PRODUCT_STOCK,
                DatabaseHelper.COLUMN_PRODUCT_CATEGORY, DatabaseHelper.COLUMN_PRODUCT_SALE_TYPE,
                DatabaseHelper.COLUMN_PRODUCT_PACKAGE_QUANTITY,
                DatabaseHelper.COLUMN_PRODUCT_WEIGHT
            ),
            null, null, null, null, DatabaseHelper.COLUMN_PRODUCT_NAME
        )

        cursor.use {
            while (it.moveToNext()) {
                val weightIndex = it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_WEIGHT)
                products.add(

                    Product(
                        id = it.getInt(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                        name = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_NAME)),
                        description = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_DESCRIPTION)),
                        price = it.getDouble(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_PRICE)),
                        imageUrl = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_IMAGE_URL)) ?: "",
                        stock = it.getDouble(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_STOCK)),
                        category = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_CATEGORY)),
                        saleType = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_SALE_TYPE)),
                        packageQuantity = it.getDouble(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_PACKAGE_QUANTITY)) ,
                        weight = it.getDouble(weightIndex)
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