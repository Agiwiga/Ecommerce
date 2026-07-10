package com.example.ecommerce.model

data class Order(
    val id: Int,
    val totalPrice: Double,
    val createdAt: Long,
    val status: String,
    val paymentMethod: String
)
