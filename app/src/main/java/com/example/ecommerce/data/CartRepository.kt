package com.example.ecommerce.data

import android.content.ContentValues
import android.content.Context
import com.example.ecommerce.model.Cart
import com.example.ecommerce.model.Product

class CartRepository(context: Context) {
    private val databaseHelper = DatabaseHelper(context)

    fun addToCart(
        userId: Int,
        productId: Int,
        purchaseType: String,
        inputQuantity: Double,
        actualQuantity: Double,
        totalPrice: Double
    ): Boolean {
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_CART_USER_ID, userId)
            put(DatabaseHelper.COLUMN_CART_PRODUCT_ID, productId)
            put(DatabaseHelper.COLUMN_CART_PURCHASE_TYPE, purchaseType)
            put(DatabaseHelper.COLUMN_CART_QUANTITY, inputQuantity)
            put(DatabaseHelper.COLUMN_CART_ACTUAL_WEIGHT, actualQuantity)
            put(DatabaseHelper.COLUMN_CART_INPUT_QUANTITY, inputQuantity)
            put(DatabaseHelper.COLUMN_CART_ACTUAL_QUANTITY, actualQuantity)
            put(DatabaseHelper.COLUMN_CART_TOTAL_PRICE, totalPrice)
        }

        return databaseHelper.writableDatabase.insert(
            DatabaseHelper.TABLE_CART,
            null,
            values
        ) != -1L
    }

    fun getCartItems(userId: Int): List<Cart> {
        val cartItems = mutableListOf<Cart>()
        val database = databaseHelper.readableDatabase
        val query = """
            SELECT
                c.${DatabaseHelper.COLUMN_ID} AS cart_id,
                c.${DatabaseHelper.COLUMN_CART_USER_ID},
                c.${DatabaseHelper.COLUMN_CART_PRODUCT_ID},
                c.${DatabaseHelper.COLUMN_CART_PURCHASE_TYPE},
                c.${DatabaseHelper.COLUMN_CART_INPUT_QUANTITY},
                c.${DatabaseHelper.COLUMN_CART_ACTUAL_QUANTITY},
                c.${DatabaseHelper.COLUMN_CART_TOTAL_PRICE},
                p.${DatabaseHelper.COLUMN_PRODUCT_NAME},
                p.${DatabaseHelper.COLUMN_PRODUCT_DESCRIPTION},
                p.${DatabaseHelper.COLUMN_PRODUCT_PRICE},
                p.${DatabaseHelper.COLUMN_PRODUCT_IMAGE_URL},
                p.${DatabaseHelper.COLUMN_PRODUCT_STOCK},
                p.${DatabaseHelper.COLUMN_PRODUCT_CATEGORY},
                p.${DatabaseHelper.COLUMN_PRODUCT_SALE_TYPE},
                p.${DatabaseHelper.COLUMN_PRODUCT_PACKAGE_QUANTITY}
            FROM ${DatabaseHelper.TABLE_CART} c
            INNER JOIN ${DatabaseHelper.TABLE_PRODUCTS} p
                ON c.${DatabaseHelper.COLUMN_CART_PRODUCT_ID} = p.${DatabaseHelper.COLUMN_ID}
            WHERE c.${DatabaseHelper.COLUMN_CART_USER_ID} = ?
            ORDER BY c.${DatabaseHelper.COLUMN_ID} DESC
        """.trimIndent()

        val cursor = database.rawQuery(query, arrayOf(userId.toString()))
        cursor.use {
            while (it.moveToNext()) {
                val productId = it.getInt(
                    it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CART_PRODUCT_ID)
                )
                val product = Product(
                    id = productId,
                    name = it.getString(
                        it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_NAME)
                    ),
                    description = it.getString(
                        it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_DESCRIPTION)
                    ),
                    price = it.getDouble(
                        it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_PRICE)
                    ),
                    imageUrl = it.getString(
                        it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_IMAGE_URL)
                    ) ?: "",
                    stock = it.getDouble(
                        it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_STOCK)
                    ),
                    category = it.getString(
                        it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_CATEGORY)
                    ),
                    saleType = it.getString(
                        it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_SALE_TYPE)
                    ),
                    packageQuantity = it.getDouble(
                        it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_PACKAGE_QUANTITY)
                    )
                )

                cartItems.add(
                    Cart(
                        id = it.getInt(it.getColumnIndexOrThrow("cart_id")),
                        userId = it.getInt(
                            it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CART_USER_ID)
                        ),
                        productId = productId,
                        purchaseType = it.getString(
                            it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CART_PURCHASE_TYPE)
                        ),
                        inputQuantity = it.getDouble(
                            it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CART_INPUT_QUANTITY)
                        ),
                        actualQuantity = it.getDouble(
                            it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CART_ACTUAL_QUANTITY)
                        ),
                        totalPrice = it.getDouble(
                            it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CART_TOTAL_PRICE)
                        ),
                        product = product
                    )
                )
            }
        }

        return cartItems
    }

    fun deleteCartItem(cartId: Int): Boolean {
        return databaseHelper.writableDatabase.delete(
            DatabaseHelper.TABLE_CART,
            "${DatabaseHelper.COLUMN_ID} = ?",
            arrayOf(cartId.toString())
        ) > 0
    }

    fun updateCartItem(
        cartId: Int,
        purchaseType: String,
        inputQuantity: Double,
        actualQuantity: Double,
        totalPrice: Double
    ): Boolean {
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_CART_PURCHASE_TYPE, purchaseType)
            put(DatabaseHelper.COLUMN_CART_QUANTITY, inputQuantity)
            put(DatabaseHelper.COLUMN_CART_ACTUAL_WEIGHT, actualQuantity)
            put(DatabaseHelper.COLUMN_CART_INPUT_QUANTITY, inputQuantity)
            put(DatabaseHelper.COLUMN_CART_ACTUAL_QUANTITY, actualQuantity)
            put(DatabaseHelper.COLUMN_CART_TOTAL_PRICE, totalPrice)
        }

        return databaseHelper.writableDatabase.update(
            DatabaseHelper.TABLE_CART,
            values,
            "${DatabaseHelper.COLUMN_ID} = ?",
            arrayOf(cartId.toString())
        ) > 0
    }

    fun clearCart(userId: Int): Boolean {
        databaseHelper.writableDatabase.delete(
            DatabaseHelper.TABLE_CART,
            "${DatabaseHelper.COLUMN_CART_USER_ID} = ?",
            arrayOf(userId.toString())
        )
        return true
    }
}
