package com.example.ecommerce.data

import android.content.Context
import android.database.Cursor
import com.example.ecommerce.model.ProfitReport

class ProfitRepository(context: Context) {

    private val databaseHelper = DatabaseHelper(context)

    fun getProfitReport(): ProfitReport {

        val db = databaseHelper.readableDatabase

        var totalSales = 0.0
        var totalCapital = 0.0

        val query = """
            SELECT
                oi.${DatabaseHelper.COLUMN_ORDER_ITEM_PRODUCT_ID},
                oi.${DatabaseHelper.COLUMN_ORDER_ITEM_ACTUAL_QUANTITY},
                oi.${DatabaseHelper.COLUMN_ORDER_ITEM_TOTAL_PRICE}
            FROM ${DatabaseHelper.TABLE_ORDER_ITEMS} oi
        """.trimIndent()

        val cursor = db.rawQuery(query, null)

        cursor.use {

            while (it.moveToNext()) {

                val productId = it.getInt(0)
                val quantity = it.getDouble(1)
                val totalPrice = it.getDouble(2)

                totalSales += totalPrice

                val purchasePrice = getLatestPurchasePrice(productId)

                totalCapital += purchasePrice * quantity

            }

        }

        val totalProfit = totalSales - totalCapital

        return ProfitReport(
            totalSales = totalSales,
            totalCapital = totalCapital,
            totalProfit = totalProfit
        )

    }
    private fun getLatestPurchasePrice(productId: Int): Double {

        val db = databaseHelper.readableDatabase

        val cursor: Cursor = db.query(
            DatabaseHelper.TABLE_RESTOCK,
            arrayOf(DatabaseHelper.COLUMN_RESTOCK_PURCHASE_PRICE),
            "${DatabaseHelper.COLUMN_RESTOCK_PRODUCT_ID} = ?",
            arrayOf(productId.toString()),
            null,
            null,
            "${DatabaseHelper.COLUMN_RESTOCK_CREATED_AT} DESC",
            "1"
        )
        cursor.use {
            return if (it.moveToFirst()) {
                it.getDouble(
                    it.getColumnIndexOrThrow(
                        DatabaseHelper.COLUMN_RESTOCK_PURCHASE_PRICE
                    )
                )
            } else {
                0.0
            }
        }
    }
}