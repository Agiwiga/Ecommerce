package com.example.ecommerce.model

data class Product(
    val id: Int = 0,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String = "",
    val stock: Double,

    val category: String,
    val saleType: String,
    val packageQuantity: Double
)