package com.example.ecommerce.ui

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ecommerce.R
import com.example.ecommerce.data.CartRepository
import com.example.ecommerce.data.CheckoutRepository
import com.example.ecommerce.session.SessionManager
import java.text.NumberFormat
import java.util.Locale

class CheckoutActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager
    private lateinit var cartRepository: CartRepository
    private lateinit var checkoutRepository: CheckoutRepository
    private lateinit var summaryTextView: TextView
    private lateinit var checkoutButton: Button
    private lateinit var cancelButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        sessionManager = SessionManager(this)
        cartRepository = CartRepository(this)
        checkoutRepository = CheckoutRepository(this)
        summaryTextView = findViewById(R.id.textViewCheckoutSummary)
        checkoutButton = findViewById(R.id.buttonConfirmCheckout)
        cancelButton = findViewById(R.id.buttonCancelCheckout)

        showSummary()

        checkoutButton.setOnClickListener {
            processCheckout()
        }

        cancelButton.setOnClickListener {
            finish()
        }
    }

    private fun showSummary() {
        val items = cartRepository.getCartItems(sessionManager.getUserId())
        val subtotal = items.sumOf { it.totalPrice }
        summaryTextView.text = "Jumlah item: ${items.size}\nTotal harga: ${formatPrice(subtotal)}"
        checkoutButton.isEnabled = items.isNotEmpty()
    }

    private fun processCheckout() {
        val result = checkoutRepository.checkout(
            sessionManager.getUserId(),
            "COD"
        )
        Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
        if (result.success) {
            finish()
        }
    }

    private fun formatPrice(price: Double): String {
        return NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID")).format(price)
    }
}
