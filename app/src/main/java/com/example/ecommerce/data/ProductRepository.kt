package com.example.ecommerce.data

import android.content.Context
import com.example.ecommerce.model.Product

class ProductRepository(context: Context) {

    private val databaseHelper = DatabaseHelper(context)

    fun getProducts(): List<Product> {
        val products = mutableListOf<Product>()
        val database = databaseHelper.readableDatabase

        val cursor = database.query(
            DatabaseHelper.TABLE_PRODUCTS,
            arrayOf(
                DatabaseHelper.COLUMN_ID,
                DatabaseHelper.COLUMN_PRODUCT_NAME,
                DatabaseHelper.COLUMN_PRODUCT_DESCRIPTION,
                DatabaseHelper.COLUMN_PRODUCT_PRICE,
                DatabaseHelper.COLUMN_PRODUCT_IMAGE_URL,
                DatabaseHelper.COLUMN_PRODUCT_STOCK,
                DatabaseHelper.COLUMN_PRODUCT_CATEGORY,
                DatabaseHelper.COLUMN_PRODUCT_SALE_TYPE,
                DatabaseHelper.COLUMN_PRODUCT_PACKAGE_QUANTITY,
                DatabaseHelper.COLUMN_PRODUCT_WEIGHT
            ),
            null,
            null,
            null,
            null,
            DatabaseHelper.COLUMN_PRODUCT_NAME
        )

        cursor.use {
            while (it.moveToNext()) {
                products.add(
                    Product(
                        id = it.getInt(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                        name = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_NAME)),
                        description = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_DESCRIPTION)),
                        price = it.getDouble(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_PRICE)),
                        imageUrl = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_IMAGE_URL)) ?: "",
                        stock = it.getDouble(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_STOCK)),
                        category = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_CATEGORY)),
                        saleType = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_SALE_TYPE)),
                        packageQuantity = it.getDouble(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_PACKAGE_QUANTITY)),
                        weight = it.getDouble(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_WEIGHT))
                    )
                )
            }
        }

        return products
    }

    fun searchProducts(keyword: String): List<Product> {

        val products = mutableListOf<Product>()
        val database = databaseHelper.readableDatabase

        val cursor = database.query(
            DatabaseHelper.TABLE_PRODUCTS,
            arrayOf(
                DatabaseHelper.COLUMN_ID,
                DatabaseHelper.COLUMN_PRODUCT_NAME,
                DatabaseHelper.COLUMN_PRODUCT_DESCRIPTION,
                DatabaseHelper.COLUMN_PRODUCT_PRICE,
                DatabaseHelper.COLUMN_PRODUCT_IMAGE_URL,
                DatabaseHelper.COLUMN_PRODUCT_STOCK,
                DatabaseHelper.COLUMN_PRODUCT_CATEGORY,
                DatabaseHelper.COLUMN_PRODUCT_SALE_TYPE,
                DatabaseHelper.COLUMN_PRODUCT_PACKAGE_QUANTITY,
                DatabaseHelper.COLUMN_PRODUCT_WEIGHT
            ),
            "${DatabaseHelper.COLUMN_PRODUCT_NAME} LIKE ?",
            arrayOf("%$keyword%"),
            null,
            null,
            DatabaseHelper.COLUMN_PRODUCT_NAME
        )

        cursor.use {
            while (it.moveToNext()) {
                products.add(
                    Product(
                        id = it.getInt(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                        name = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_NAME)),
                        description = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_DESCRIPTION)),
                        price = it.getDouble(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_PRICE)),
                        imageUrl = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_IMAGE_URL)) ?: "",
                        stock = it.getDouble(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_STOCK)),
                        category = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_CATEGORY)),
                        saleType = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_SALE_TYPE)),
                        packageQuantity = it.getDouble(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_PACKAGE_QUANTITY)),
                        weight = it.getDouble(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_WEIGHT))
                    )
                )
            }
        }

        return products
    }

    fun getProductById(productId: Int): Product? {

        val database = databaseHelper.readableDatabase

        val cursor = database.query(
            DatabaseHelper.TABLE_PRODUCTS,
            arrayOf(
                DatabaseHelper.COLUMN_ID,
                DatabaseHelper.COLUMN_PRODUCT_NAME,
                DatabaseHelper.COLUMN_PRODUCT_DESCRIPTION,
                DatabaseHelper.COLUMN_PRODUCT_PRICE,
                DatabaseHelper.COLUMN_PRODUCT_IMAGE_URL,
                DatabaseHelper.COLUMN_PRODUCT_STOCK,
                DatabaseHelper.COLUMN_PRODUCT_CATEGORY,
                DatabaseHelper.COLUMN_PRODUCT_SALE_TYPE,
                DatabaseHelper.COLUMN_PRODUCT_PACKAGE_QUANTITY,
                DatabaseHelper.COLUMN_PRODUCT_WEIGHT
            ),
            "${DatabaseHelper.COLUMN_ID} = ?",
            arrayOf(productId.toString()),
            null,
            null,
            null,
            "1"
        )

        cursor.use {
            return if (it.moveToFirst()) {

                Product(
                    id = it.getInt(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                    name = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_NAME)),
                    description = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_DESCRIPTION)),
                    price = it.getDouble(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_PRICE)),
                    imageUrl = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_IMAGE_URL)) ?: "",
                    stock = it.getDouble(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_STOCK)),
                    category = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_CATEGORY)),
                    saleType = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_SALE_TYPE)),
                    packageQuantity = it.getDouble(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_PACKAGE_QUANTITY)),
                    weight = it.getDouble(it.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PRODUCT_WEIGHT))
                )

            } else {
                null
            }
        }
    }
}