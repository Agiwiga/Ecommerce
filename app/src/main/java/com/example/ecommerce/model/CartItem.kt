package com.example.ecommerce.model

data class CartItem(
    val id: Int = 0,
    val userId: Int,
    val productId: Int,
    val quantity: Int
)
