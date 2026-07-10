package com.example.ecommerce.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log

class CheckoutRepository(context: Context) {
    private val databaseHelper = DatabaseHelper(context)
    private val cartRepository = CartRepository(context)

    fun checkout(
        userId: Int,
        paymentMethod: String
    ): CheckoutResult {
        val cartItems = cartRepository.getCartItems(userId)
        Log.d(TAG, "Mulai checkout userId=$userId, jumlah cart=${cartItems.size}")
        cartItems.forEach { item ->
            Log.d(
                TAG,
                "Cart item id=${item.id}, productId=${item.productId}, purchaseType=${item.purchaseType}, input=${item.inputQuantity}, actual=${item.actualQuantity}, total=${item.totalPrice}, joinedStock=${item.product?.stock}"
            )
        }

        if (cartItems.isEmpty()) {
            return CheckoutResult(false, "Keranjang masih kosong")
        }

        val database = databaseHelper.writableDatabase
        database.beginTransaction()
        try {
            val totalPrice = cartItems.sumOf { it.totalPrice }

            val orderStatus = if (paymentMethod == "COD") {
                "Diproses"
            } else {
                "Menunggu Pembayaran"
            }
            val orderId = database.insert(
                DatabaseHelper.TABLE_ORDERS,
                null,
                ContentValues().apply {
                    put(DatabaseHelper.COLUMN_ORDER_USER_ID, userId)
                    put(DatabaseHelper.COLUMN_ORDER_TOTAL_PRICE, totalPrice)
                    put(DatabaseHelper.COLUMN_ORDER_CREATED_AT, System.currentTimeMillis())
                    put(DatabaseHelper.COLUMN_ORDER_STATUS, orderStatus)
                    put(
                        DatabaseHelper.COLUMN_ORDER_PAYMENT_METHOD,
                        paymentMethod
                    )
                }
            )

            if (orderId == -1L) {
                return CheckoutResult(false, "Checkout gagal")
            }

            for (item in cartItems) {
                val product = item.product ?: return CheckoutResult(false, "Produk tidak ditemukan")
                val currentStock = getLatestProductStock(database, product.id)
                    ?: return CheckoutResult(false, "Produk ${product.name} tidak ditemukan")
                val stockToReduce = calculateStockToReduce(
                    actualQuantity = item.actualQuantity,
                    packageQuantity = product.packageQuantity
                )
                val newStock = currentStock - stockToReduce

                Log.d(
                    TAG,
                    "Update stok productId=${product.id}, stokSebelum=$currentStock, stockToReduce=$stockToReduce, stokBaru=$newStock"
                )

                if (newStock < 0.0) {
                    return CheckoutResult(false, "Stok ${product.name} tidak cukup")
                }

                val updatedRows = database.update(
                    DatabaseHelper.TABLE_PRODUCTS,
                    ContentValues().apply {
                        put(DatabaseHelper.COLUMN_PRODUCT_STOCK, newStock)
                    },
                    "${DatabaseHelper.COLUMN_ID} = ?",
                    arrayOf(product.id.toString())
                )
                Log.d(TAG, "SQLite update rows untuk productId=${product.id}: $updatedRows")

                if (updatedRows <= 0) {
                    return CheckoutResult(false, "Stok ${product.name} gagal diperbarui")
                }

                val insertedOrderItem = database.insert(
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
                Log.d(TAG, "Insert order item result=$insertedOrderItem untuk cartId=${item.id}")

                if (insertedOrderItem == -1L) {
                    return CheckoutResult(false, "Item order gagal disimpan")
                }
            }

            val deletedCartRows = database.delete(
                DatabaseHelper.TABLE_CART,
                "${DatabaseHelper.COLUMN_CART_USER_ID} = ?",
                arrayOf(userId.toString())
            )
            Log.d(TAG, "Cart rows dihapus setelah checkout: $deletedCartRows")

            database.setTransactionSuccessful()
            return CheckoutResult(true, "Checkout berhasil")
        } finally {
            database.endTransaction()
        }
    }

    private fun getLatestProductStock(database: SQLiteDatabase, productId: Int): Double? {
        val cursor = database.query(
            DatabaseHelper.TABLE_PRODUCTS,
            arrayOf(DatabaseHelper.COLUMN_PRODUCT_STOCK),
            "${DatabaseHelper.COLUMN_ID} = ?",
            arrayOf(productId.toString()),
            null,
            null,
            null,
            "1"
        )

        cursor.use {
            return if (it.moveToFirst()) {
                it.getDouble(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_STOCK))
            } else {
                null
            }
        }
    }

    private fun calculateStockToReduce(actualQuantity: Double, packageQuantity: Double): Double {
        return if (packageQuantity > 0.0) {
            actualQuantity / packageQuantity
        } else {
            0.0
        }
    }

    data class CheckoutResult(
        val success: Boolean,
        val message: String
    )

    companion object {
        private const val TAG = "CheckoutRepository"
    }
}
