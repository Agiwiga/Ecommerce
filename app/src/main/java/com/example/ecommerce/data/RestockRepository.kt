package com.example.ecommerce.data

import android.content.Context
import com.example.ecommerce.model.Product
import com.example.ecommerce.model.Supplier
import android.content.ContentValues

class RestockRepository(
    context: Context
) {

    private val databaseHelper = DatabaseHelper(context)
    private val supplierRepository = SupplierRepository(context)
    fun getSuppliers(): List<Supplier> {
        return supplierRepository.getAllSuppliers()
    }
    fun saveRestock(
        supplierId: Int,
        product: Product,
        quantity: Double,
        purchasePrice: Double
    ): Boolean {

        val db = databaseHelper.writableDatabase

        db.beginTransaction()

        try {

            val totalCost = quantity * purchasePrice

            val restockValues = ContentValues().apply {
                put(DatabaseHelper.COLUMN_RESTOCK_SUPPLIER_ID, supplierId)
                put(DatabaseHelper.COLUMN_RESTOCK_PRODUCT_ID, product.id)
                put(DatabaseHelper.COLUMN_RESTOCK_QUANTITY, quantity)
                put(DatabaseHelper.COLUMN_RESTOCK_PURCHASE_PRICE, purchasePrice)
                put(DatabaseHelper.COLUMN_RESTOCK_TOTAL_COST, totalCost)
                put(DatabaseHelper.COLUMN_RESTOCK_CREATED_AT, System.currentTimeMillis())
            }

            val restockInserted = db.insert(
                DatabaseHelper.TABLE_RESTOCK,
                null,
                restockValues
            )

            if (restockInserted == -1L) {
                return false
            }

            val newStock = product.stock + quantity

            val productValues = ContentValues().apply {
                put(DatabaseHelper.COLUMN_PRODUCT_STOCK, newStock)
            }

            db.update(
                DatabaseHelper.TABLE_PRODUCTS,
                productValues,
                "${DatabaseHelper.COLUMN_ID} = ?",
                arrayOf(product.id.toString())
            )

            db.setTransactionSuccessful()

            return true

        } finally {

            db.endTransaction()

        }
    }
}
