package com.example.ecommerce.data

import android.content.Context
import com.example.ecommerce.model.Order
import com.example.ecommerce.model.OrderItem

class OrderRepository(context: Context) {

    private val databaseHelper = DatabaseHelper(context)

    fun getOrdersByUser(userId: Int): List<Order> {

        val orders = mutableListOf<Order>()

        val database = databaseHelper.readableDatabase

        val cursor = database.query(
            DatabaseHelper.TABLE_ORDERS,
            arrayOf(
                DatabaseHelper.COLUMN_ID,
                DatabaseHelper.COLUMN_ORDER_TOTAL_PRICE,
                DatabaseHelper.COLUMN_ORDER_CREATED_AT,
                DatabaseHelper.COLUMN_ORDER_STATUS,
                DatabaseHelper.COLUMN_ORDER_PAYMENT_METHOD
            ),
            "${DatabaseHelper.COLUMN_ORDER_USER_ID} = ?",
            arrayOf(userId.toString()),
            null,
            null,
            "${DatabaseHelper.COLUMN_ORDER_CREATED_AT} DESC"
        )

        cursor.use {

            while (it.moveToNext()) {

                orders.add(
                    Order(
                        id = it.getInt(
                            it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)
                        ),
                        totalPrice = it.getDouble(
                            it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ORDER_TOTAL_PRICE)
                        ),
                        createdAt = it.getLong(
                            it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ORDER_CREATED_AT)
                        ),
                        status = it.getString(
                            it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ORDER_STATUS)
                        ),
                        paymentMethod = it.getString(
                            it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ORDER_PAYMENT_METHOD)
                        )
                    )
                )

            }

        }

        return orders
    }

    fun getOrderById(orderId: Int, userId: Int): Order? {

        val database = databaseHelper.readableDatabase

        val cursor = database.query(
            DatabaseHelper.TABLE_ORDERS,
            arrayOf(
                DatabaseHelper.COLUMN_ID,
                DatabaseHelper.COLUMN_ORDER_TOTAL_PRICE,
                DatabaseHelper.COLUMN_ORDER_CREATED_AT,
                DatabaseHelper.COLUMN_ORDER_STATUS,
                DatabaseHelper.COLUMN_ORDER_PAYMENT_METHOD
            ),
            "${DatabaseHelper.COLUMN_ID} = ? AND ${DatabaseHelper.COLUMN_ORDER_USER_ID} = ?",
            arrayOf(orderId.toString(), userId.toString()),
            null,
            null,
            null,
            "1"
        )

        cursor.use {

            return if (it.moveToFirst()) {

                Order(
                    id = it.getInt(
                        it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)
                    ),
                    totalPrice = it.getDouble(
                        it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ORDER_TOTAL_PRICE)
                    ),
                    createdAt = it.getLong(
                        it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ORDER_CREATED_AT)
                    ),
                    status = it.getString(
                        it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ORDER_STATUS)
                    ),
                    paymentMethod = it.getString(
                        it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ORDER_PAYMENT_METHOD)
                    )
                )

            } else {

                null

            }

        }

    }

    fun getOrderItems(orderId: Int): List<OrderItem> {

        val items = mutableListOf<OrderItem>()

        val database = databaseHelper.readableDatabase

        val query = """
            SELECT
                oi.${DatabaseHelper.COLUMN_ID} AS order_item_id,
                oi.${DatabaseHelper.COLUMN_ORDER_ITEM_ORDER_ID},
                oi.${DatabaseHelper.COLUMN_ORDER_ITEM_PRODUCT_ID},
                oi.${DatabaseHelper.COLUMN_ORDER_ITEM_PURCHASE_TYPE},
                oi.${DatabaseHelper.COLUMN_ORDER_ITEM_INPUT_QUANTITY},
                oi.${DatabaseHelper.COLUMN_ORDER_ITEM_ACTUAL_QUANTITY},
                oi.${DatabaseHelper.COLUMN_ORDER_ITEM_TOTAL_PRICE},
                p.${DatabaseHelper.COLUMN_PRODUCT_NAME},
                p.${DatabaseHelper.COLUMN_PRODUCT_PRICE}
            FROM ${DatabaseHelper.TABLE_ORDER_ITEMS} oi
            LEFT JOIN ${DatabaseHelper.TABLE_PRODUCTS} p
                ON oi.${DatabaseHelper.COLUMN_ORDER_ITEM_PRODUCT_ID} = p.${DatabaseHelper.COLUMN_ID}
            WHERE oi.${DatabaseHelper.COLUMN_ORDER_ITEM_ORDER_ID} = ?
            ORDER BY oi.${DatabaseHelper.COLUMN_ID} ASC
        """.trimIndent()

        val cursor = database.rawQuery(
            query,
            arrayOf(orderId.toString())
        )

        cursor.use {

            while (it.moveToNext()) {

                val productNameIndex =
                    it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_NAME)

                val productPriceIndex =
                    it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_PRICE)

                items.add(
                    OrderItem(
                        id = it.getInt(
                            it.getColumnIndexOrThrow("order_item_id")
                        ),
                        orderId = it.getInt(
                            it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ORDER_ITEM_ORDER_ID)
                        ),
                        productId = it.getInt(
                            it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ORDER_ITEM_PRODUCT_ID)
                        ),
                        productName = if (it.isNull(productNameIndex)) {
                            "Produk tidak tersedia"
                        } else {
                            it.getString(productNameIndex)
                        },
                        purchaseType = it.getString(
                            it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ORDER_ITEM_PURCHASE_TYPE)
                        ),
                        inputQuantity = it.getDouble(
                            it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ORDER_ITEM_INPUT_QUANTITY)
                        ),
                        actualQuantity = it.getDouble(
                            it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ORDER_ITEM_ACTUAL_QUANTITY)
                        ),
                        itemPrice = if (it.isNull(productPriceIndex)) {
                            0.0
                        } else {
                            it.getDouble(productPriceIndex)
                        },
                        totalPrice = it.getDouble(
                            it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ORDER_ITEM_TOTAL_PRICE)
                        )
                    )
                )

            }

        }

        return items
    }
}