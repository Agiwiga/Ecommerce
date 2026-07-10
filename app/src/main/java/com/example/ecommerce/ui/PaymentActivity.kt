package com.example.ecommerce.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ecommerce.R
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Toast
import android.content.Intent
import com.example.ecommerce.data.CheckoutRepository
import com.example.ecommerce.session.SessionManager

class PaymentActivity : AppCompatActivity() {
    private lateinit var radioGroupPayment: RadioGroup
    private lateinit var continueButton: Button

    private lateinit var checkoutRepository: CheckoutRepository
    private lateinit var sessionManager: SessionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        radioGroupPayment = findViewById(R.id.radioGroupPayment)
        continueButton = findViewById(R.id.buttonContinuePayment)
        checkoutRepository = CheckoutRepository(this)
        sessionManager = SessionManager(this)

        continueButton.setOnClickListener {
            val checkedId = radioGroupPayment.checkedRadioButtonId

            if (checkedId == -1) {

                Toast.makeText(
                    this,
                    "Pilih metode pembayaran terlebih dahulu",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val paymentMethod = when (checkedId) {

                R.id.radioTransfer -> "Transfer Bank"

                R.id.radioQris -> "QRIS"

                else -> "COD"
            }
            val result = checkoutRepository.checkout(
                sessionManager.getUserId(),
                paymentMethod
            )

            Toast.makeText(
                this,
                result.message,
                Toast.LENGTH_SHORT
            ).show()

            if (result.success) {
                finish()
            }


        }
    }
}
