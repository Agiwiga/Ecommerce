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
            products = getProducts(),
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
        }
        startActivity(intent)
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
}
