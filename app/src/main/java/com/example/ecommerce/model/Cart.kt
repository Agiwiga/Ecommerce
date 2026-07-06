package com.example.ecommerce.model

data class Cart(
    val id: Int = 0,
    val userId: Int,
    val productId: Int,
    val purchaseType: String,
    val inputQuantity: Double,
    val actualQuantity: Double,
    val totalPrice: Double,
    val product: Product? = null
)
