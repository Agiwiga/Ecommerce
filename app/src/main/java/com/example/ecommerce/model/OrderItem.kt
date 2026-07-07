package com.example.ecommerce.model

data class OrderItem(
    val id: Int = 0,
    val orderId: Int,
    val productId: Int,
    val productName: String,
    val purchaseType: String,
    val inputQuantity: Double,
    val actualQuantity: Double,
    val itemPrice: Double,
    val totalPrice: Double
)
