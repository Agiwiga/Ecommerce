package com.example.ecommerce.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ecommerce.R
import com.example.ecommerce.data.ProductRepository
import com.example.ecommerce.data.RestockRepository
import com.example.ecommerce.data.SupplierRepository
import com.example.ecommerce.model.Product
import com.example.ecommerce.model.Supplier
import java.text.NumberFormat
import java.util.Locale

class RestockActivity : AppCompatActivity() {

    private lateinit var supplierSpinner: Spinner
    private lateinit var productSpinner: Spinner

    private lateinit var quantityEditText: EditText
    private lateinit var purchasePriceEditText: EditText

    private lateinit var totalCostTextView: TextView
    private lateinit var saveButton: Button

    private lateinit var supplierRepository: SupplierRepository
    private lateinit var productRepository: ProductRepository
    private lateinit var restockRepository: RestockRepository

    private var suppliers = listOf<Supplier>()
    private var products = listOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restock)

        supplierSpinner = findViewById(R.id.spinnerSupplier)
        productSpinner = findViewById(R.id.spinnerProduct)

        quantityEditText = findViewById(R.id.editTextQuantity)
        purchasePriceEditText = findViewById(R.id.editTextPurchasePrice)

        totalCostTextView = findViewById(R.id.textViewTotalCost)
        saveButton = findViewById(R.id.buttonSaveRestock)

        supplierRepository = SupplierRepository(this)
        productRepository = ProductRepository(this)
        restockRepository = RestockRepository(this)

        loadSuppliers()
        loadProducts()

        setupCalculation()

        saveButton.setOnClickListener {
            saveRestock()
        }
    }

    private fun loadSuppliers() {

        suppliers = supplierRepository.getAllSuppliers()

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            suppliers.map { it.name }
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        supplierSpinner.adapter = adapter
    }

    private fun loadProducts() {

        products = productRepository.getProducts()

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            products.map { it.name }
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        productSpinner.adapter = adapter
    }

    private fun setupCalculation() {

        val watcher = object : TextWatcher {

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                calculateTotalCost()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }

        quantityEditText.addTextChangedListener(watcher)
        purchasePriceEditText.addTextChangedListener(watcher)
    }

    private fun calculateTotalCost() {

        val quantity =
            quantityEditText.text.toString().toDoubleOrNull() ?: 0.0

        val purchasePrice =
            purchasePriceEditText.text.toString().toDoubleOrNull() ?: 0.0

        val total = quantity * purchasePrice

        val rupiah =
            NumberFormat.getCurrencyInstance(Locale("in", "ID"))

        totalCostTextView.text = rupiah.format(total)
    }

    private fun saveRestock() {

        if (suppliers.isEmpty() || products.isEmpty()) {
            Toast.makeText(
                this,
                "Data belum lengkap",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val quantity =
            quantityEditText.text.toString().toDoubleOrNull()

        val purchasePrice =
            purchasePriceEditText.text.toString().toDoubleOrNull()

        if (quantity == null || purchasePrice == null) {
            Toast.makeText(
                this,
                "Lengkapi semua data",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val supplier =
            suppliers[supplierSpinner.selectedItemPosition]

        val product =
            products[productSpinner.selectedItemPosition]

        val success = restockRepository.saveRestock(
            supplier.id,
            product,
            quantity,
            purchasePrice
        )

        if (success) {

            Toast.makeText(
                this,
                "Restock berhasil",
                Toast.LENGTH_SHORT
            ).show()

            quantityEditText.text.clear()
            purchasePriceEditText.text.clear()

            totalCostTextView.text = "Rp0"

            loadProducts()

        } else {

            Toast.makeText(
                this,
                "Restock gagal",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}