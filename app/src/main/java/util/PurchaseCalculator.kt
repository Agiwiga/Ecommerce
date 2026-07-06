package com.example.ecommerce.util

import com.example.ecommerce.model.Product

object PurchaseCalculator {

    data class Result(
        val quantity: Double,
        val actualWeight: Double,
        val totalPrice: Double
    )

    fun calculate(
        product: Product,
        purchaseType: String,
        quantity: Double
    ): Result {

        // sementara nanti kita isi
        return Result(
            quantity = quantity,
            actualWeight = quantity,
            totalPrice = product.price * quantity
        )
    }
}