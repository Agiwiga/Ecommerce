package com.example.ecommerce.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerce.R
import com.example.ecommerce.adapter.CartAdapter
import com.example.ecommerce.data.CartRepository
import com.example.ecommerce.model.Cart
import com.example.ecommerce.session.SessionManager
import java.text.NumberFormat
import java.util.Locale

class CartActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager
    private lateinit var cartRepository: CartRepository
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyTextView: TextView
    private lateinit var subtotalTextView: TextView
    private lateinit var itemCountTextView: TextView
    private lateinit var checkoutButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        sessionManager = SessionManager(this)
        cartRepository = CartRepository(this)
        recyclerView = findViewById(R.id.recyclerViewCart)
        emptyTextView = findViewById(R.id.textViewEmptyCart)
        subtotalTextView = findViewById(R.id.textViewCartSubtotal)
        itemCountTextView = findViewById(R.id.textViewCartItemCount)
        checkoutButton = findViewById(R.id.buttonContinueCheckout)

        checkoutButton.setOnClickListener {
            startActivity(Intent(this, CheckoutActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        showCart()
    }

    private fun showCart() {
        val items = cartRepository.getCartItems(sessionManager.getUserId())
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = CartAdapter(
            items = items,
            onEditClick = { item -> openEditCartItem(item) },
            onDeleteClick = { item -> confirmDelete(item) }
        )

        emptyTextView.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
        recyclerView.visibility = if (items.isEmpty()) View.GONE else View.VISIBLE
        checkoutButton.isEnabled = items.isNotEmpty()
        subtotalTextView.text = "Subtotal: ${formatPrice(items.sumOf { it.totalPrice })}"
        itemCountTextView.text = "Jumlah item: ${items.size}"
    }

    private fun openEditCartItem(item: Cart) {
        val product = item.product ?: return
        val intent = Intent(this, ProductDetailActivity::class.java).apply {
            putExtra(ProductDetailActivity.EXTRA_CART_ID, item.id)
            putExtra(ProductDetailActivity.EXTRA_PURCHASE_TYPE, item.purchaseType)
            putExtra(ProductDetailActivity.EXTRA_INPUT_QUANTITY, item.inputQuantity)
            putExtra(ProductDetailActivity.EXTRA_PRODUCT_ID, product.id)
            putExtra(ProductDetailActivity.EXTRA_PRODUCT_NAME, product.name)
            putExtra(ProductDetailActivity.EXTRA_PRODUCT_DESCRIPTION, product.description)
            putExtra(ProductDetailActivity.EXTRA_PRODUCT_CATEGORY, product.category)
            putExtra(ProductDetailActivity.EXTRA_PRODUCT_SALE_TYPE, product.saleType)
            putExtra(ProductDetailActivity.EXTRA_PRODUCT_PRICE, product.price)
            putExtra(ProductDetailActivity.EXTRA_PRODUCT_PACKAGE_QUANTITY, product.packageQuantity)
            putExtra(ProductDetailActivity.EXTRA_PRODUCT_STOCK, product.stock)
        }
        startActivity(intent)
    }

    private fun confirmDelete(item: Cart) {
        AlertDialog.Builder(this)
            .setMessage("Hapus item ini dari keranjang?")
            .setPositiveButton("Ya") { dialog, _ ->
                if (cartRepository.deleteCartItem(item.id)) {
                    Toast.makeText(this, "Item berhasil dihapus", Toast.LENGTH_SHORT).show()
                    showCart()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun formatPrice(price: Double): String {
        return NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID")).format(price)
    }
}
