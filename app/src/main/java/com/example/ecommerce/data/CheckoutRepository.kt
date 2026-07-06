package com.example.ecommerce.data

import android.content.ContentValues
import android.content.Context

class CheckoutRepository(context: Context) {
    private val databaseHelper = DatabaseHelper(context)
    private val cartRepository = CartRepository(context)

    fun checkout(userId: Int): CheckoutResult {
        val cartItems = cartRepository.getCartItems(userId)
        if (cartItems.isEmpty()) {
            return CheckoutResult(false, "Keranjang masih kosong")
        }

        val database = databaseHelper.writableDatabase
        database.beginTransaction()
        try {
            val totalPrice = cartItems.sumOf { it.totalPrice }

            for (item in cartItems) {
                val product = item.product ?: return CheckoutResult(false, "Produk tidak ditemukan")
                val stockToReduce = if (product.packageQuantity > 0) {
                    item.actualQuantity / product.packageQuantity
                } else {
                    0.0
                }
                val newStock = product.stock - stockToReduce
                if (newStock < 0.0) {
                    return CheckoutResult(false, "Stok ${product.name} tidak cukup")
                }
            }

            val orderId = database.insert(
                DatabaseHelper.TABLE_ORDERS,
                null,
                ContentValues().apply {
                    put(DatabaseHelper.COLUMN_ORDER_USER_ID, userId)
                    put(DatabaseHelper.COLUMN_ORDER_TOTAL_PRICE, totalPrice)
                    put(DatabaseHelper.COLUMN_ORDER_CREATED_AT, System.currentTimeMillis())
                    put(DatabaseHelper.COLUMN_ORDER_STATUS, "Diproses")
                }
            )

            if (orderId == -1L) {
                return CheckoutResult(false, "Checkout gagal")
            }

            for (item in cartItems) {
                val product = item.product ?: return CheckoutResult(false, "Produk tidak ditemukan")
                val stockToReduce = item.actualQuantity / product.packageQuantity
                val newStock = product.stock - stockToReduce

                database.update(
                    DatabaseHelper.TABLE_PRODUCTS,
                    ContentValues().apply {
                        put(DatabaseHelper.COLUMN_PRODUCT_STOCK, newStock)
                    },
                    "${DatabaseHelper.COLUMN_ID} = ?",
                    arrayOf(product.id.toString())
                )

                database.insert(
                    DatabaseHelper.TABLE_ORDER_ITEMS,
                    null,
                    ContentValues().apply {
                        put(DatabaseHelper.COLUMN_ORDER_ITEM_ORDER_ID, orderId)
                        put(DatabaseHelper.COLUMN_ORDER_ITEM_PRODUCT_ID, item.productId)
                        put(DatabaseHelper.COLUMN_ORDER_ITEM_PURCHASE_TYPE, item.purchaseType)
                        put(DatabaseHelper.COLUMN_ORDER_ITEM_INPUT_QUANTITY, item.inputQuantity)
                        put(DatabaseHelper.COLUMN_ORDER_ITEM_ACTUAL_QUANTITY, item.actualQuantity)
                        put(DatabaseHelper.COLUMN_ORDER_ITEM_TOTAL_PRICE, item.totalPrice)
                    }
                )
            }

            database.delete(
                DatabaseHelper.TABLE_CART,
                "${DatabaseHelper.COLUMN_CART_USER_ID} = ?",
                arrayOf(userId.toString())
            )

            database.setTransactionSuccessful()
            return CheckoutResult(true, "Checkout berhasil")
        } finally {
            database.endTransaction()
        }
    }

    data class CheckoutResult(
        val success: Boolean,
        val message: String
    )
}
