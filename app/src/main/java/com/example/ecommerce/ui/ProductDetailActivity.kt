package com.example.ecommerce.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.ecommerce.R
import java.text.NumberFormat
import java.util.Locale

class ProductDetailActivity : AppCompatActivity() {
    private lateinit var nameTextView: TextView
    private lateinit var categoryTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var unitPriceTextView: TextView
    private lateinit var packageInfoTextView: TextView
    private lateinit var stockTextView: TextView
    private lateinit var purchaseTypeTextView: TextView
    private lateinit var quantityLabelTextView: TextView
    private lateinit var quantityEditText: EditText
    private lateinit var nominalEditText: EditText
    private lateinit var estimatedWeightTextView: TextView
    private lateinit var validationTextView: TextView
    private lateinit var totalPriceTextView: TextView
    private lateinit var purchaseTypeRadioGroup: RadioGroup
    private lateinit var firstPurchaseOptionRadioButton: RadioButton
    private lateinit var secondPurchaseOptionRadioButton: RadioButton
    private lateinit var nominalPurchaseOptionRadioButton: RadioButton
    private lateinit var decreaseQuantityButton: Button
    private lateinit var increaseQuantityButton: Button

    private var productName: String = ""
    private var productDescription: String = ""
    private var productCategory: String = ""
    private var productSaleType: String = ""
    private var productPrice: Double = 0.0
    private var productPackageQuantity: Double = 0.0
    private var productStock: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        bindViews()
        readProductData()
        showProductData()
        setupPurchaseOptions()
        setupRealtimeCalculation()
        updatePurchaseSummary()
    }

    private fun bindViews() {
        nameTextView = findViewById(R.id.textViewDetailProductName)
        categoryTextView = findViewById(R.id.textViewDetailProductCategory)
        descriptionTextView = findViewById(R.id.textViewDetailProductDescription)
        unitPriceTextView = findViewById(R.id.textViewDetailUnitPrice)
        packageInfoTextView = findViewById(R.id.textViewDetailPackageInfo)
        stockTextView = findViewById(R.id.textViewDetailStock)
        purchaseTypeTextView = findViewById(R.id.textViewSelectedPurchaseType)
        quantityLabelTextView = findViewById(R.id.textViewQuantityLabel)
        quantityEditText = findViewById(R.id.editTextPurchaseQuantity)
        nominalEditText = findViewById(R.id.editTextPurchaseNominal)
        estimatedWeightTextView = findViewById(R.id.textViewEstimatedWeight)
        validationTextView = findViewById(R.id.textViewPurchaseValidation)
        totalPriceTextView = findViewById(R.id.textViewTotalPrice)
        purchaseTypeRadioGroup = findViewById(R.id.radioGroupPurchaseType)
        firstPurchaseOptionRadioButton = findViewById(R.id.radioButtonFirstPurchaseOption)
        secondPurchaseOptionRadioButton = findViewById(R.id.radioButtonSecondPurchaseOption)
        nominalPurchaseOptionRadioButton = findViewById(R.id.radioButtonNominalPurchaseOption)
        decreaseQuantityButton = findViewById(R.id.buttonDecreaseQuantity)
        increaseQuantityButton = findViewById(R.id.buttonIncreaseQuantity)
    }

    private fun readProductData() {
        productName = intent.getStringExtra(EXTRA_PRODUCT_NAME).orEmpty()
        productDescription = intent.getStringExtra(EXTRA_PRODUCT_DESCRIPTION).orEmpty()
        productCategory = intent.getStringExtra(EXTRA_PRODUCT_CATEGORY).orEmpty()
        productSaleType = intent.getStringExtra(EXTRA_PRODUCT_SALE_TYPE).orEmpty()
        productPrice = intent.getDoubleExtra(EXTRA_PRODUCT_PRICE, 0.0)
        productPackageQuantity = intent.getDoubleExtra(EXTRA_PRODUCT_PACKAGE_QUANTITY, 0.0)
        productStock = intent.getDoubleExtra(EXTRA_PRODUCT_STOCK, 0.0)
    }

    private fun showProductData() {
        nameTextView.text = productName
        categoryTextView.text = "Kategori: $productCategory"
        descriptionTextView.text = productDescription

        if (isWeightProduct()) {
            unitPriceTextView.text = "Harga satuan: ${formatPrice(productPrice)} / Kg"
            packageInfoTextView.text =
                "Isi kemasan: ${formatQuantity(productPackageQuantity)} Kg per karung"
            stockTextView.text =
                "Stok: ${formatQuantity(productStock)} karung (${formatQuantity(getAvailableBaseUnit())} Kg)"
        } else {
            unitPriceTextView.text = "Harga satuan: ${formatPrice(productPrice)} / Pcs"
            packageInfoTextView.text =
                "Isi kemasan: ${formatQuantity(productPackageQuantity)} pcs per pack"
            stockTextView.text =
                "Stok: ${formatQuantity(productStock)} pack (${formatQuantity(getAvailableBaseUnit())} pcs)"
        }
    }

    private fun setupPurchaseOptions() {
        if (isWeightProduct()) {
            firstPurchaseOptionRadioButton.text = "Berdasarkan Berat (Kg)"
            secondPurchaseOptionRadioButton.text = "Berdasarkan Karung"
            nominalPurchaseOptionRadioButton.visibility = View.VISIBLE
            setQuantityText("0.5")
        } else {
            firstPurchaseOptionRadioButton.text = "Berdasarkan Pcs"
            secondPurchaseOptionRadioButton.text = "Berdasarkan Pack"
            nominalPurchaseOptionRadioButton.visibility = View.GONE
            setQuantityText("1")
        }

        firstPurchaseOptionRadioButton.isChecked = true
        updateInputMode()
    }

    private fun setupRealtimeCalculation() {
        purchaseTypeRadioGroup.setOnCheckedChangeListener { _, _ ->
            updateInputMode()
            updatePurchaseSummary()
        }

        quantityEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updatePurchaseSummary()
            }

            override fun afterTextChanged(s: Editable?) = Unit
        })

        nominalEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updatePurchaseSummary()
            }

            override fun afterTextChanged(s: Editable?) = Unit
        })

        decreaseQuantityButton.setOnClickListener {
            changeQuantity(-getQuantityStep())
        }

        increaseQuantityButton.setOnClickListener {
            changeQuantity(getQuantityStep())
        }
    }

    private fun updateInputMode() {
        purchaseTypeTextView.text = "Mode pembelian: ${getSelectedPurchaseTypeLabel()}"
        validationTextView.text = ""

        if (isNominalPurchaseSelected()) {
            quantityLabelTextView.text = "Nominal uang"
            quantityEditText.visibility = View.GONE
            decreaseQuantityButton.visibility = View.GONE
            increaseQuantityButton.visibility = View.GONE
            nominalEditText.visibility = View.VISIBLE
            estimatedWeightTextView.visibility = View.VISIBLE
            if (nominalEditText.text.isNullOrBlank()) {
                nominalEditText.setText("")
            }
        } else {
            quantityLabelTextView.text = "Jumlah yang dibeli"
            quantityEditText.visibility = View.VISIBLE
            decreaseQuantityButton.visibility = View.VISIBLE
            increaseQuantityButton.visibility = View.VISIBLE
            nominalEditText.visibility = View.GONE
            estimatedWeightTextView.visibility = View.GONE
            estimatedWeightTextView.text = ""
            normalizeQuantityForSelectedMode()
        }
    }

    private fun normalizeQuantityForSelectedMode() {
        val quantity = getQuantityInput()
        val minimum = getMinimumQuantity()
        if (quantity < minimum) {
            setQuantityText(formatQuantity(minimum))
        }
    }

    private fun updatePurchaseSummary() {
        val result = calculatePurchase()
        validationTextView.text = result.validationMessage
        estimatedWeightTextView.text = result.estimatedText
        totalPriceTextView.text = "Total harga: ${formatPrice(result.totalPrice)}"
    }

    private fun calculatePurchase(): PurchaseCalculation {
        if (productPrice <= 0.0) {
            return PurchaseCalculation(validationMessage = "Harga produk belum valid")
        }

        val baseUnitAmount: Double
        val totalPrice: Double
        var estimatedText = ""

        if (isNominalPurchaseSelected()) {
            val nominal = nominalEditText.text.toString().toDoubleOrNull() ?: 0.0
            if (nominal < 0.0) {
                return PurchaseCalculation(validationMessage = "Nominal tidak boleh negatif")
            }

            baseUnitAmount = nominal / productPrice
            totalPrice = nominal
            estimatedText = "Estimasi berat: ${formatQuantity(baseUnitAmount)} Kg"
        } else {
            val quantity = getQuantityInput()
            baseUnitAmount = if (isPackagePurchaseSelected()) {
                quantity * productPackageQuantity
            } else {
                quantity
            }
            totalPrice = baseUnitAmount * productPrice
        }

        val validationMessage = validatePurchase(baseUnitAmount)
        return PurchaseCalculation(
            totalPrice = totalPrice,
            estimatedText = estimatedText,
            validationMessage = validationMessage
        )
    }

    private fun validatePurchase(baseUnitAmount: Double): String {
        if (baseUnitAmount <= 0.0) {
            return if (isNominalPurchaseSelected()) {
                "Nominal harus menghasilkan berat lebih dari 0 Kg"
            } else {
                "Jumlah pembelian tidak boleh 0"
            }
        }

        val availableBaseUnit = getAvailableBaseUnit()
        if (baseUnitAmount > availableBaseUnit) {
            val unit = if (isWeightProduct()) "Kg" else "pcs"
            return "Jumlah melebihi stok. Stok tersedia hanya ${formatQuantity(availableBaseUnit)} $unit."
        }

        return ""
    }

    private fun changeQuantity(delta: Double) {
        val currentQuantity = getQuantityInput()
        val nextQuantity = (currentQuantity + delta).coerceAtLeast(getMinimumQuantity())
        setQuantityText(formatQuantity(nextQuantity))
    }

    private fun getQuantityStep(): Double {
        return if (isWeightProduct() && isBaseUnitPurchaseSelected()) 0.5 else 1.0
    }

    private fun getMinimumQuantity(): Double {
        return if (isWeightProduct() && isBaseUnitPurchaseSelected()) 0.5 else 1.0
    }

    private fun getQuantityInput(): Double {
        return quantityEditText.text.toString().toDoubleOrNull() ?: 0.0
    }

    private fun setQuantityText(value: String) {
        quantityEditText.setText(value)
        quantityEditText.setSelection(quantityEditText.text.length)
    }

    private fun getAvailableBaseUnit(): Double {
        return productStock * productPackageQuantity
    }

    private fun isWeightProduct(): Boolean {
        return productSaleType == SALE_TYPE_WEIGHT
    }

    private fun isBaseUnitPurchaseSelected(): Boolean {
        return purchaseTypeRadioGroup.checkedRadioButtonId == R.id.radioButtonFirstPurchaseOption
    }

    private fun isPackagePurchaseSelected(): Boolean {
        return purchaseTypeRadioGroup.checkedRadioButtonId == R.id.radioButtonSecondPurchaseOption
    }

    private fun isNominalPurchaseSelected(): Boolean {
        return purchaseTypeRadioGroup.checkedRadioButtonId == R.id.radioButtonNominalPurchaseOption
    }

    private fun getSelectedPurchaseTypeLabel(): String {
        val selectedRadioButton = findViewById<RadioButton>(purchaseTypeRadioGroup.checkedRadioButtonId)
        return selectedRadioButton?.text?.toString().orEmpty()
    }

    private fun formatPrice(price: Double): String {
        return NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID")).format(price)
    }

    private fun formatQuantity(quantity: Double): String {
        return if (quantity % 1.0 == 0.0) {
            quantity.toInt().toString()
        } else {
            String.format(Locale.US, "%.2f", quantity).trimEnd('0').trimEnd('.')
        }
    }

    private data class PurchaseCalculation(
        val totalPrice: Double = 0.0,
        val estimatedText: String = "",
        val validationMessage: String = ""
    )

    companion object {
        const val EXTRA_PRODUCT_NAME = "extra_product_name"
        const val EXTRA_PRODUCT_DESCRIPTION = "extra_product_description"
        const val EXTRA_PRODUCT_CATEGORY = "extra_product_category"
        const val EXTRA_PRODUCT_SALE_TYPE = "extra_product_sale_type"
        const val EXTRA_PRODUCT_PRICE = "extra_product_price"
        const val EXTRA_PRODUCT_PACKAGE_QUANTITY = "extra_product_package_quantity"
        const val EXTRA_PRODUCT_STOCK = "extra_product_stock"

        private const val SALE_TYPE_WEIGHT = "Berat"
    }
}
