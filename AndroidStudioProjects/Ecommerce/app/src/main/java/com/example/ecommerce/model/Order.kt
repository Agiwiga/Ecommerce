package com.example.ecommerce.model

data class Order(
    val id: Int = 0,
    val userId: Int,
    val totalPrice: Double,
    val createdAt: Long,
    val status: String
)
