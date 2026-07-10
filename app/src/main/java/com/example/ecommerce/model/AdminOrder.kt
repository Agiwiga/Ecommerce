package com.example.ecommerce.model

data class AdminOrder(
    val id: Int,
    val userId: Int,
    val customerName: String,
    val customerEmail: String,
    val totalPrice: Double,
    val createdAt: Long,
    val status: String,
    val paymentMethod: String
)
