package com.example.ecommerce.data

import android.content.Context
import com.example.ecommerce.model.Restock


class RestockHistoryRepository(
    context: Context
) {

    private val databaseHelper = DatabaseHelper(context)

    fun getAllRestock(): List<Restock> {

        val restockList = mutableListOf<Restock>()

        val db = databaseHelper.readableDatabase

        val query = """
        SELECT
            r.${DatabaseHelper.COLUMN_ID},
            s.${DatabaseHelper.COLUMN_SUPPLIER_NAME},
            p.${DatabaseHelper.COLUMN_PRODUCT_NAME},
            r.${DatabaseHelper.COLUMN_RESTOCK_QUANTITY},
            r.${DatabaseHelper.COLUMN_RESTOCK_PURCHASE_PRICE},
            r.${DatabaseHelper.COLUMN_RESTOCK_TOTAL_COST},
            r.${DatabaseHelper.COLUMN_RESTOCK_CREATED_AT}
        FROM ${DatabaseHelper.TABLE_RESTOCK} r
        INNER JOIN ${DatabaseHelper.TABLE_SUPPLIERS} s
            ON r.${DatabaseHelper.COLUMN_RESTOCK_SUPPLIER_ID} = s.${DatabaseHelper.COLUMN_ID}
        INNER JOIN ${DatabaseHelper.TABLE_PRODUCTS} p
            ON r.${DatabaseHelper.COLUMN_RESTOCK_PRODUCT_ID} = p.${DatabaseHelper.COLUMN_ID}
        ORDER BY r.${DatabaseHelper.COLUMN_RESTOCK_CREATED_AT} DESC
    """.trimIndent()

        val cursor = db.rawQuery(query, null)

        cursor.use {

            while (it.moveToNext()) {

                restockList.add(
                    Restock(
                        id = it.getInt(
                            it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)
                        ),
                        supplierName = it.getString(
                            it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUPPLIER_NAME)
                        ),
                        productName = it.getString(
                            it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_NAME)
                        ),
                        quantity = it.getDouble(
                            it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RESTOCK_QUANTITY)
                        ),
                        purchasePrice = it.getDouble(
                            it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RESTOCK_PURCHASE_PRICE)
                        ),
                        totalCost = it.getDouble(
                            it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RESTOCK_TOTAL_COST)
                        ),
                        createdAt = it.getLong(
                            it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RESTOCK_CREATED_AT)
                        )
                    )
                )
            }
        }

        return restockList
    }
}