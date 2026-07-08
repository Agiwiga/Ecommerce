package com.example.ecommerce.data

import android.content.Context
import com.example.ecommerce.model.Supplier

class SupplierRepository(context: Context) {

    private val databaseHelper = DatabaseHelper(context)

    fun getAllSuppliers(): List<Supplier> {
        val suppliers = mutableListOf<Supplier>()

        val db = databaseHelper.readableDatabase

        val cursor = db.query(
            DatabaseHelper.TABLE_SUPPLIERS,
            null,
            null,
            null,
            null,
            null,
            "${DatabaseHelper.COLUMN_SUPPLIER_NAME} ASC"
        )

        cursor.use {
            while (it.moveToNext()) {

                suppliers.add(
                    Supplier(
                        id = it.getInt(
                            it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)
                        ),
                        name = it.getString(
                            it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUPPLIER_NAME)
                        ),
                        address = it.getString(
                            it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUPPLIER_ADDRESS)
                        ),
                        phone = it.getString(
                            it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SUPPLIER_PHONE)
                        )
                    )
                )
            }
        }

        return suppliers
    }

    fun addSupplier(
        name: String,
        address: String,
        phone: String
    ): Boolean {

        val values = android.content.ContentValues().apply {
            put(DatabaseHelper.COLUMN_SUPPLIER_NAME, name)
            put(DatabaseHelper.COLUMN_SUPPLIER_ADDRESS, address)
            put(DatabaseHelper.COLUMN_SUPPLIER_PHONE, phone)
        }

        return databaseHelper.writableDatabase.insert(
            DatabaseHelper.TABLE_SUPPLIERS,
            null,
            values
        ) != -1L
    }
    fun updateSupplier(
        id: Int,
        name: String,
        address: String,
        phone: String
    ): Boolean {
        val values = android.content.ContentValues().apply {
            put(DatabaseHelper.COLUMN_SUPPLIER_NAME, name)
            put(DatabaseHelper.COLUMN_SUPPLIER_ADDRESS, address)
            put(DatabaseHelper.COLUMN_SUPPLIER_PHONE, phone)
        }

        val updatedRows = databaseHelper.writableDatabase.update(
            DatabaseHelper.TABLE_SUPPLIERS,
            values,
            "${DatabaseHelper.COLUMN_ID} = ?",
            arrayOf(id.toString())
        )

        return updatedRows > 0
    }
    fun deleteSupplier(id: Int): Boolean {
        val deletedRows = databaseHelper.writableDatabase.delete(
            DatabaseHelper.TABLE_SUPPLIERS,
            "${DatabaseHelper.COLUMN_ID} = ?",
            arrayOf(id.toString())
        )
        return deletedRows > 0
    }
}