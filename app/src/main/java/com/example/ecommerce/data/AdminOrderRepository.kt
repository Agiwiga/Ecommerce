package com.example.ecommerce.data

import android.content.ContentValues
import android.content.Context
import com.example.ecommerce.model.AdminOrder
import com.example.ecommerce.model.OrderItem

class AdminOrderRepository(context: Context) {

    private val databaseHelper = DatabaseHelper(context)
    private val orderRepository = OrderRepository(context)

    fun getAllOrders(): List<AdminOrder> {

        val orders = mutableListOf<AdminOrder>()

        val database = databaseHelper.readableDatabase

        val query = """
            SELECT
                o.${DatabaseHelper.COLUMN_ID} AS order_id,
                o.${DatabaseHelper.COLUMN_ORDER_USER_ID},
                o.${DatabaseHelper.COLUMN_ORDER_TOTAL_PRICE},
                o.${DatabaseHelper.COLUMN_ORDER_CREATED_AT},
                o.${DatabaseHelper.COLUMN_ORDER_STATUS},
                o.${DatabaseHelper.COLUMN_ORDER_PAYMENT_METHOD},
                u.${DatabaseHelper.COLUMN_USER_NAME},
                u.${DatabaseHelper.COLUMN_USER_EMAIL}
            FROM ${DatabaseHelper.TABLE_ORDERS} o
            LEFT JOIN ${DatabaseHelper.TABLE_USERS} u
                ON o.${DatabaseHelper.COLUMN_ORDER_USER_ID} = u.${DatabaseHelper.COLUMN_ID}
            ORDER BY o.${DatabaseHelper.COLUMN_ORDER_CREATED_AT} DESC
        """.trimIndent()

        val cursor = database.rawQuery(query, null)

        cursor.use {

            while (it.moveToNext()) {

                val nameIndex =
                    it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_NAME)

                val emailIndex =
                    it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_EMAIL)

                orders.add(

                    AdminOrder(

                        id = it.getInt(
                            it.getColumnIndexOrThrow("order_id")
                        ),

                        userId = it.getInt(
                            it.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_ORDER_USER_ID
                            )
                        ),

                        customerName =
                            if (it.isNull(nameIndex))
                                "Customer tidak tersedia"
                            else
                                it.getString(nameIndex),

                        customerEmail =
                            if (it.isNull(emailIndex))
                                "-"
                            else
                                it.getString(emailIndex),

                        totalPrice = it.getDouble(
                            it.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_ORDER_TOTAL_PRICE
                            )
                        ),

                        createdAt = it.getLong(
                            it.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_ORDER_CREATED_AT
                            )
                        ),

                        status = it.getString(
                            it.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_ORDER_STATUS
                            )
                        ),

                        paymentMethod = it.getString(
                            it.getColumnIndexOrThrow(
                                DatabaseHelper.COLUMN_ORDER_PAYMENT_METHOD
                            )
                        )

                    )

                )

            }

        }

        return orders
    }

    fun getOrderById(orderId: Int): AdminOrder? {

        val database = databaseHelper.readableDatabase

        val query = """
            SELECT
                o.${DatabaseHelper.COLUMN_ID} AS order_id,
                o.${DatabaseHelper.COLUMN_ORDER_USER_ID},
                o.${DatabaseHelper.COLUMN_ORDER_TOTAL_PRICE},
                o.${DatabaseHelper.COLUMN_ORDER_CREATED_AT},
                o.${DatabaseHelper.COLUMN_ORDER_STATUS},
                o.${DatabaseHelper.COLUMN_ORDER_PAYMENT_METHOD},
                u.${DatabaseHelper.COLUMN_USER_NAME},
                u.${DatabaseHelper.COLUMN_USER_EMAIL}
            FROM ${DatabaseHelper.TABLE_ORDERS} o
            LEFT JOIN ${DatabaseHelper.TABLE_USERS} u
                ON o.${DatabaseHelper.COLUMN_ORDER_USER_ID} = u.${DatabaseHelper.COLUMN_ID}
            WHERE o.${DatabaseHelper.COLUMN_ID} = ?
            LIMIT 1
        """.trimIndent()

        val cursor = database.rawQuery(
            query,
            arrayOf(orderId.toString())
        )

        cursor.use {

            return if (it.moveToFirst()) {

                val nameIndex =
                    it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_NAME)

                val emailIndex =
                    it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_EMAIL)

                AdminOrder(

                    id = it.getInt(
                        it.getColumnIndexOrThrow("order_id")
                    ),

                    userId = it.getInt(
                        it.getColumnIndexOrThrow(
                            DatabaseHelper.COLUMN_ORDER_USER_ID
                        )
                    ),

                    customerName =
                        if (it.isNull(nameIndex))
                            "Customer tidak tersedia"
                        else
                            it.getString(nameIndex),

                    customerEmail =
                        if (it.isNull(emailIndex))
                            "-"
                        else
                            it.getString(emailIndex),

                    totalPrice = it.getDouble(
                        it.getColumnIndexOrThrow(
                            DatabaseHelper.COLUMN_ORDER_TOTAL_PRICE
                        )
                    ),

                    createdAt = it.getLong(
                        it.getColumnIndexOrThrow(
                            DatabaseHelper.COLUMN_ORDER_CREATED_AT
                        )
                    ),

                    status = it.getString(
                        it.getColumnIndexOrThrow(
                            DatabaseHelper.COLUMN_ORDER_STATUS
                        )
                    ),

                    paymentMethod = it.getString(
                        it.getColumnIndexOrThrow(
                            DatabaseHelper.COLUMN_ORDER_PAYMENT_METHOD
                        )
                    )

                )

            } else {

                null

            }

        }

    }

    fun getOrderItems(orderId: Int): List<OrderItem> {
        return orderRepository.getOrderItems(orderId)
    }

    fun updateOrderStatus(
        orderId: Int,
        status: String
    ): Boolean {

        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_ORDER_STATUS, status)
        }

        return databaseHelper.writableDatabase.update(
            DatabaseHelper.TABLE_ORDERS,
            values,
            "${DatabaseHelper.COLUMN_ID} = ?",
            arrayOf(orderId.toString())
        ) > 0
    }

}