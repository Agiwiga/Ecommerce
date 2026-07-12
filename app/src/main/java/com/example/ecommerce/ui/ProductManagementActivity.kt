package com.example.ecommerce.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerce.R
import com.example.ecommerce.adapter.AdminProductAdapter
import com.example.ecommerce.data.DatabaseHelper
import com.example.ecommerce.model.Product
import com.example.ecommerce.data.ProductRepository

class ProductManagementActivity : AppCompatActivity() {
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var productRepository: ProductRepository
    private lateinit var productsRecyclerView: RecyclerView
    private lateinit var addProductButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_management)

        databaseHelper = DatabaseHelper(this)
        productRepository = ProductRepository(this)
        productsRecyclerView = findViewById(R.id.recyclerViewAdminProducts)
        addProductButton = findViewById(R.id.buttonAddProduct)
        addProductButton.setOnClickListener {
            startActivity(Intent(this, AddProductActivity::class.java))
        }

    }

    override fun onResume() {
        super.onResume()
        showProducts()
    }

    private fun showProducts() {
        val adapter = AdminProductAdapter(
            products = productRepository.getProducts(),
            onProductClick = { product ->
                openEditProduct(product)
            },
            onDeleteClick = { product ->
                showDeleteConfirmation(product)
            }
        )

        productsRecyclerView.layoutManager = LinearLayoutManager(this)
        productsRecyclerView.adapter = adapter
    }

    private fun showDeleteConfirmation(product: Product) {
        AlertDialog.Builder(this)
            .setMessage("Apakah Anda yakin ingin menghapus produk ini?")
            .setPositiveButton("Ya") { dialog, _ ->
                deleteProduct(product)
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun deleteProduct(product: Product) {
        val deletedRows = databaseHelper.writableDatabase.delete(
            DatabaseHelper.TABLE_PRODUCTS,
            "${DatabaseHelper.COLUMN_ID} = ?",
            arrayOf(product.id.toString())
        )

        if (deletedRows > 0) {
            Toast.makeText(this, "Produk berhasil dihapus", Toast.LENGTH_SHORT).show()
            showProducts()
        } else {
            Toast.makeText(this, "Produk gagal dihapus", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openEditProduct(product: Product) {
        val intent = Intent(this, EditProductActivity::class.java).apply {
            putExtra(EditProductActivity.EXTRA_PRODUCT_ID, product.id)
            putExtra(EditProductActivity.EXTRA_PRODUCT_NAME, product.name)
            putExtra(EditProductActivity.EXTRA_PRODUCT_PRICE, product.price)
            putExtra(EditProductActivity.EXTRA_PRODUCT_DESCRIPTION, product.description)
            putExtra(EditProductActivity.EXTRA_PRODUCT_CATEGORY, product.category)
            putExtra(EditProductActivity.EXTRA_PRODUCT_SALE_TYPE, product.saleType)
            putExtra(EditProductActivity.EXTRA_PRODUCT_PACKAGE_QUANTITY, product.packageQuantity)
            putExtra(EditProductActivity.EXTRA_PRODUCT_WEIGHT, product.weight)
            putExtra(EditProductActivity.EXTRA_PRODUCT_STOCK, product.stock)
        }
        startActivity(intent)
    }
}
