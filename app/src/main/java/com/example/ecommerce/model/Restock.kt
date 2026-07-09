package com.example.ecommerce.model

data class Restock(
    val id: Int,
    val supplierName: String,
    val productName: String,
    val quantity: Double,
    val purchasePrice: Double,
    val totalCost: Double,
    val createdAt: Long
)